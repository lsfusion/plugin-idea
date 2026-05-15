package com.lsfusion.mcp

import com.intellij.mcpserver.McpExpectedError
import com.intellij.mcpserver.annotations.McpDescription
import com.intellij.mcpserver.annotations.McpTool
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.io.HttpRequests
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.coroutines.coroutineContext

@Serializable
data class FindElementCodePart(
    @McpDescription(description = "Element location in source. Format: `<module>(<line>:<col>)` (1-based), e.g. `MyModule(10:5)`.")
    val location: String? = null,
    @McpDescription(description = "Element source code snippet. May be shortened depending on output size limits.")
    val code: String? = null,
    @McpDescription(description = "Stack of enclosing `META` blocks (headers), from nearest to farthest. Omitted when element is not inside `META`.")
    val metacodeStack: List<String>? = null,
)

@Serializable
data class FindElementItem(
    /** `LSFMCPDeclaration.ElementType.apiName` (e.g. `property`, `class`, ...). */
    @McpDescription(description = "Element type, e.g. `property`, `class`, `action`, ...")
    val type: String? = null,
    /** Canonical name (best-effort), when available. */
    @McpDescription(description = "Canonical element name (best-effort). Omitted for unnamed statements.")
    val name: String? = null,
    /** Additional names (e.g. from `EXTEND`), canonical names. */
    @McpDescription(description = "Additional canonical names for the same declaration (e.g. from `EXTEND`).")
    val moreNames: List<String>? = null,

    // Main statement code part (same fields as in `extends[*]`).
    @McpDescription(description = "Location of the main statement. Format: `<module>(<line>:<col>)` (1-based).")
    val location: String? = null,
    @McpDescription(description = "Main statement code snippet. May be shortened depending on output size limits.")
    val code: String? = null,
    @McpDescription(description = "Stack of enclosing `META` blocks for the main statement (headers), from nearest to farthest.")
    val metacodeStack: List<String>? = null,

    /** Code fragments for `EXTEND`-ed declarations (without `type`/`name`). */
    @McpDescription(description = "Code fragments for `EXTEND`-ed declarations of the same element. Each entry contains its own `location`, `code`, and `metacodeStack`.")
    val extends: List<FindElementCodePart>? = null,
)

@Serializable
data class FindElementsResult(
    @McpDescription(description = "Found elements (each item represents one declaration plus optional `EXTEND` fragments).")
    val items: List<FindElementItem>,
    @McpDescription(description = "Optional single-line meta reason (e.g. `too long - timeout hit`, `too large - max symbols hit`, `too small - non matching elements added`).")
    val meta: String? = null,
    @McpDescription(description = "List of errors encountered during search (e.g. timeouts, internal errors).")
    val errors: List<String>? = null,
)

@Serializable
data class RemoteDocItem(
    @McpDescription(description = "Chunk origin (e.g. documentation-language, documentation-paradigm).")
    val source: String,
    @McpDescription(description = "Retrieved text snippet.")
    val text: String,
    @McpDescription(description = "Similarity score (higher = more relevant).")
    val score: Double,
)

@Serializable
data class RetrieveDocsOutput(
    @McpDescription(description = "Relevant chunks returned from the RAG store.")
    val docs: List<RemoteDocItem>,
)


/**
 * MCP toolset for IntelliJ MCP Server plugin.
 *
 * NOTE: Implemented in Kotlin so that JetBrains `mcpserver` (which uses Kotlin reflection
 * for schema generation) can expose stable parameter names in `tools/list`.
 *
 * TODO (AI agent task): Keep the list of proxied tools from https://ai.lsfusion.org/mcp up to date.
 * IMPORTANT: When updating AI tools, ensure that both McpServerService.java and McpToolset.kt are synchronized.
 * These tools are proxied from https://ai.lsfusion.org, and their descriptors must be manually updated in both files.
 *
 * Background:
 * - We intentionally expose remote MCP tools as explicit local tools (not a generic JSON-RPC proxy),
 *   so IntelliJ MCP `tools/list` shows stable names, descriptions, and proper input/output schemas.
 * - The remote server uses MCP sessions: you MUST call `initialize` first and then pass the
 *   `mcp-session-id` header on subsequent requests.
 * - The remote server requires `Accept: application/json, text/event-stream` and may still respond
 *   with SSE (`text/event-stream`), so we extract JSON from the first `data:` line.
 *
 * How to refresh when remote tools/schemas change:
 * 1) Fetch remote tool descriptors:
 *    - POST https://ai.lsfusion.org/mcp method=`initialize` to obtain `mcp-session-id` header.
 *    - POST method=`tools/list` with `mcp-session-id` and required Accept header.
 * 2) Compare remote `tools[*].name`, `description`, `inputSchema`, and `outputSchema` with the local
 *    wrappers (currently: `lsfusion_retrieve_docs`, `lsfusion_get_guidance`).
 * 3) For each tool:
 *    - Update/add a corresponding `@McpTool` wrapper method.
 *    - Update/add `@Serializable` DTOs with `@McpDescription` on ALL fields so output schema is not empty.
 *    - Keep parsing tolerant (`ignoreUnknownKeys = true`) because remote schemas can evolve.
 * 4) Validate manually by running IDEA MCP `tools/list` and checking that parameter names and
 *    input/output schemas match expectations.
 */
class McpToolset : com.intellij.mcpserver.McpToolset {

    private val json = Json {
        // Remote tools and MCPSearchUtils JSON may evolve; keep MCP robust.
        ignoreUnknownKeys = true
    }


    private fun callRemoteToolResultJson(toolName: String, arguments: JSONObject): JsonElement {
        val res = RemoteMcpClient.callRemoteTool(toolName, arguments)
        return try {
            json.parseToJsonElement(res)
        } catch (e: Exception) {
            JsonPrimitive(res)
        }
    }

    private suspend fun getProjectFromMcpCallContextOrNull(): Project? {
        // `mcpserver` exposes call info in coroutine context, but the helper lives in its own jar.
        // To keep compilation working even when the helper jar isn't on the build classpath,
        // access it reflectively.
        return try {
            val kt = Class.forName("com.intellij.mcpserver.McpCallInfoKt")
            val m = kt.getMethod("getProjectOrNull", kotlin.coroutines.CoroutineContext::class.java)
            m.invoke(null, coroutineContext) as? Project
        } catch (_: Throwable) {
            null
        }
    }

    private fun mcpExpectedError(message: String): McpExpectedError {
        return RemoteMcpClient.mcpExpectedError(message)
    }

    @McpTool(name = "lsfusion_find_elements")
    @McpDescription(description = "Find and inspect lsFusion elements. Results are prioritized (modules/classes > properties > actions > forms > others) and automatically truncated to a 'brief' (keeping key parts) to fit maxSymbols. If elements cannot be found (e.g. by name), search with minimal filters to explore.")
    @Suppress("unused")
    suspend fun findElements(
        @McpDescription(description = "Module filter as CSV (comma-separated), e.g. `ModuleA, ModuleB`.")
        modules: String? = null,
        @McpDescription(
            description = "Include required lsFusion modules for specified lsFusion modules (only if 'modules' is provided). Default: true."
        )
        requiredModules: Boolean = true,
        @McpDescription(
            description = "Scope filter (IDEA concept): omitted = project + libraries; `project` = project content only; otherwise, a CSV list of IDEA module names."
        )
        scope: String? = null,
        @McpDescription(
            description = "Element name filter as CSV (comma-separated). Word if valid ID, else Java regex."
        )
        names: String? = null,
        @McpDescription(
            description = "Element code filter as CSV. Word if valid ID, else Java regex."
        )
        contains: String? = null,
        @McpDescription(
            description = "Element type filter as CSV. Allowed values: `module`, `metacode`, `class`, `property`, `action`, `form`, `navigatorElement`, `window`, `group`, `table`, `event`, `calculatedEvent`, `constraint`, `index`."
        )
        elementTypes: String? = null,
        @McpDescription(
            description = "Class filter as CSV (with namespace `MyNS.MyClass` or without `MyClass`). For property/action: matches parameter classes (best-effort)."
        )
        classes: String? = null,
        @McpDescription(
            description = "Related elements filter (usage-graph traversal seeds) as CSV. Each item is either `type:name` (named element) or `location` (unnamed element). `location` format: `<module>(<line>:<col>)`, 1-based, e.g. `MyModule(10:5)`."
        )
        relatedElements: String? = null,
        @McpDescription(description = "Direction for ALL `relatedElements` seeds. Allowed values: `both`, `uses`, `used`. Default: `both`.")
        relatedDirection: String? = null,
        @McpDescription(
            description = "Additional filter objects of the same structure as the root. JSON array string (e.g. `[{\"names\":\"Foo\", \"modules\" : \"MyModule\"},{\"names\":\"Bar\"}]`). Results are merged (OR)."
        )
        moreFilters: String? = null,
        @McpDescription(description = "Best-effort minimum output size in JSON chars; server may append neighboring elements if too small (>= 0). Default: ${MCPSearchUtils.DEFAULT_MIN_SYMBOLS}.")
        minSymbols: Int = MCPSearchUtils.DEFAULT_MIN_SYMBOLS,
        @McpDescription(description = "Hard cap for total output size in JSON chars (>= 1). Default: ${MCPSearchUtils.DEFAULT_MAX_SYMBOLS}.")
        maxSymbols: Int = MCPSearchUtils.DEFAULT_MAX_SYMBOLS,
        @McpDescription(description = "Best-effort wall-clock timeout in seconds (>= 1). Default: ${MCPSearchUtils.DEFAULT_TIMEOUT_SECS}.")
        timeoutSeconds: Int = MCPSearchUtils.DEFAULT_TIMEOUT_SECS,
    ): FindElementsResult {
        // Prefer project from MCP call context (correct when multiple projects are open).
        // Fall back to "first open project" if MCP call context doesn't expose project in this build.
        val project = getProjectFromMcpCallContextOrNull()
            ?: ProjectManager.getInstance().openProjects.firstOrNull()

        if (project == null || project.isDisposed) {
            throw mcpExpectedError("No open project to run LSF MCP search on")
        }

        try {
            val query = JSONObject()
            if (modules != null) query.put("modules", modules)
            if (scope != null) query.put("scope", scope)
            query.put("requiredModules", requiredModules)
            if (names != null) query.put("names", names)
            if (contains != null) query.put("contains", contains)
            if (elementTypes != null) query.put("elementTypes", elementTypes)
            if (classes != null) query.put("classes", classes)
            if (relatedElements != null) query.put("relatedElements", relatedElements)
            if (relatedDirection != null) query.put("relatedDirection", relatedDirection)
            query.put("minSymbols", minSymbols)
            query.put("maxSymbols", maxSymbols)
            query.put("timeoutSeconds", timeoutSeconds)
            if (moreFilters != null && !moreFilters.isEmpty()) {
                query.put("moreFilters", moreFilters)
            }

            val result = MCPSearchUtils.findElements(project, query)
            val jsonElement = json.parseToJsonElement(result.toString())
            return json.decodeFromJsonElement<FindElementsResult>(jsonElement)
        } catch (e: McpExpectedError) {
            throw e
        } catch (e: Exception) {
            // Preserve user-visible error text
            throw mcpExpectedError("Invalid query or internal error: ${e.message}")
        }
    }

    @McpTool(name = "lsfusion_retrieve_docs")
    @McpDescription(description = "Search official lsFusion documentation (language reference + paradigm concepts) for chunks relevant to a query. Returns `{docs:[{source,text,score}]}` sorted by descending score. Use `type` to narrow by axis when known; omit to search both. The corpus is English-only (`docs/en/`) — cross-lingual embeddings make non-English queries work, but English wording gives the best recall.")
    @Suppress("unused")
    suspend fun retrieveDocs(
        @McpDescription(description = "Short topical phrase. Semantic match (not literal); rephrase rather than retry the same query if results are weak.")
        query: String,
        @McpDescription(description = "Optional sourceType filter. Omit (or pass null) to search both axes. `language` returns syntax / operator reference chunks; `paradigm` returns conceptual / abstraction chunks.")
        type: String? = null,
    ): RetrieveDocsOutput {
        val args = JSONObject().put("query", query)
        if (type != null) args.put("type", type)
        val resultEl = callRemoteToolResultJson("lsfusion_retrieve_docs", args)
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_get_guidance")
    @McpDescription(
        description = "Fetch the brief overview and mandatory rules for working with lsFusion. " +
            "The assistant MUST call this at the start of ANY lsFusion-related task if the guidance isn't already in context, " +
            "and MUST then read and strictly follow all rules it returns."
    )
    @Suppress("unused")
    suspend fun getGuidance(): String {
        val resultEl = callRemoteToolResultJson("lsfusion_get_guidance", JSONObject())
        return if (resultEl is JsonPrimitive) {
            resultEl.content
        } else {
            // Should not happen based on server code, but for robustness:
            resultEl.toString()
        }
    }
}
