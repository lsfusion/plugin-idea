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
    @McpDescription(description = "Chunk origin (e.g. docs, howto, tutorial).")
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

@Serializable
data class DSLError(
    @McpDescription(description = "Error category: 'syntax' or 'semantic'.")
    val type: String,
    @McpDescription(description = "Readable explanation of the problem.")
    val message: String,
    @McpDescription(description = "Line number (1-based) where error starts.")
    val line: Int,
    @McpDescription(description = "Column number (0-based) where error starts.")
    val column: Int,
    @McpDescription(description = "Actual token or fragment found, if any.")
    val found: String? = null,
    @McpDescription(description = "List of tokens or constructs expected at this point.")
    val expected: List<String> = emptyList(),
    @McpDescription(description = "Short fix suggestion to resolve the issue.")
    val hint: String? = null,
    @McpDescription(description = "Line of source where the error occurred.")
    val excerpt: String? = null,
    @McpDescription(description = "Caret marker showing error position in the excerpt.")
    val pointer: String? = null,
)

@Serializable
data class DSLValidationResult(
    @McpDescription(description = "True if DSL text parsed successfully without errors.")
    val ok: Boolean,
    @McpDescription(description = "Detailed error entries (empty if ok=True).")
    val errors: List<DSLError> = emptyList(),
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
 *    wrappers (currently: `lsfusion_retrieve_docs`, `lsfusion_retrieve_howtos`, `lsfusion_retrieve_community`,
 *    `lsfusion_validate_syntax`, `lsfusion_get_guidance`).
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
            description = "Semantic query for local RAG (vector search). CSV allowed."
        )
        query: String? = null,
        @McpDescription(
            description = "If true, use local vector search for `query`. If false, use standard filters (names/contains). Default: false."
        )
        useVectorSearch: Boolean = false,
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
            description = "Additional filter objects of the same structure as the root. JSON array string (e.g. `[{\"name\":\"Foo\", \"contains\":\"bar\", \"modules\" : \"MyModule\"},{\"query\":\"Foo\", \"useVectorSearch\":true}]`). Results are merged (OR)."
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
            val payload = JSONObject()
            if (modules != null) payload.put("modules", modules)
            if (scope != null) payload.put("scope", scope)
            payload.put("requiredModules", requiredModules)
            if (names != null) payload.put("name", names)
            if (contains != null) payload.put("contains", contains)
            if (query != null) payload.put("query", query)
            if (useVectorSearch) payload.put("useVectorSearch", true)
            if (elementTypes != null) payload.put("elementTypes", elementTypes)
            if (classes != null) payload.put("classes", classes)
            if (relatedElements != null) payload.put("relatedElements", relatedElements)
            if (relatedDirection != null) payload.put("relatedDirection", relatedDirection)
            payload.put("minSymbols", minSymbols)
            payload.put("maxSymbols", maxSymbols)
            payload.put("timeoutSeconds", timeoutSeconds)
            if (moreFilters != null && !moreFilters.isEmpty()) {
                payload.put("moreFilters", JSONArray(moreFilters))
            }

            val result = MCPSearchUtils.findElements(project, payload)
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
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (official documentation and language reference) — based on a single search query.")
    @Suppress("unused")
    suspend fun retrieveDocs(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = callRemoteToolResultJson("lsfusion_retrieve_docs", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_retrieve_howtos")
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (examples for combined tasks / scenarios and how-tos) — based on a single search query.")
    @Suppress("unused")
    suspend fun retrieveHowtos(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = callRemoteToolResultJson("lsfusion_retrieve_howtos", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_retrieve_community")
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (tutorials, articles, and community discussions) — based on a single search query. Use this ONLY for deep, ambiguous tasks when other retrieval tools (docs, howtos) did not provide a solution.")
    @Suppress("unused")
    suspend fun retrieveCommunity(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = callRemoteToolResultJson("lsfusion_retrieve_community", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_validate_syntax")
    @McpDescription(description = "Validate the syntax of the list of lsFusion statements. Use this ONLY when IDE tools for error checking and code execution are not available.")
    @Suppress("unused")
    suspend fun validateSyntax(
        @McpDescription(description = "Text")
        text: String,
    ): DSLValidationResult {
        val resultEl = callRemoteToolResultJson("lsfusion_validate_syntax", JSONObject().put("text", text))
        return json.decodeFromJsonElement<DSLValidationResult>(resultEl)
    }

    @McpTool(name = "lsfusion_get_guidance")
    @McpDescription(
        description = "Fetch the brief overview and mandatory rules for working with lsFusion. " +
            "IMPORTANT: The assistant MUST call this tool before ANY task related to lsFusion if it's not already in your context. " +
            "The assistant MUST read and strictly follow all rules and guidelines provided by this tool for ANY lsFusion-related task."
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
