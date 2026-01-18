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
 */
class McpToolset : com.intellij.mcpserver.McpToolset {

    private val json = Json {
        // Remote tools and MCPSearchUtils JSON may evolve; keep MCP robust.
        ignoreUnknownKeys = true
    }

    private class RemoteMcpClient {
        companion object {
            /*
             * TODO (AI agent task): Keep the list of proxied tools from https://ai.lsfusion.org/mcp up to date.
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
             *    wrappers below (currently: `lsfusion_retrieve_docs`, `lsfusion_retrieve_howtos_and_samples`, `lsfusion_retrieve_community`,
             *    `lsfusion_validate_syntax`, `lsfusion_get_guidance`).
             * 3) For each tool:
             *    - Update/add a corresponding `@McpTool` wrapper method.
             *    - Update/add `@Serializable` DTOs with `@McpDescription` on ALL fields so output schema is not empty.
             *    - Keep parsing tolerant (`ignoreUnknownKeys = true`) because remote schemas can evolve.
             * 4) Validate manually by running IDEA MCP `tools/list` and checking that parameter names and
             *    input/output schemas match expectations.
             */
            private const val URL = "https://ai.lsfusion.org/mcp"
            private const val DEFAULT_TIMEOUT_SECONDS = 30

            @Volatile
            private var sessionId: String? = null

            private fun extractJsonFromSseOrPlain(body: String): String {
                // Remote server may respond with `text/event-stream` even when `Accept` includes JSON.
                // Typical format:
                //   event: message\n
                //   data: { ...json... }\n
                // We extract the first `data:` line.
                val idx = body.indexOf("data:")
                if (idx >= 0) {
                    val after = body.substring(idx + "data:".length)
                    return after.lineSequence().firstOrNull()?.trim().orEmpty()
                }
                return body.trim()
            }

            internal fun mcpExpectedError(message: String): McpExpectedError {
                return try {
                    McpExpectedError::class.java.getConstructor(String::class.java, JsonObject::class.java)
                        .newInstance(message, null)
                } catch (_: Exception) {
                    McpExpectedError::class.java.getConstructor(String::class.java)
                        .newInstance(message)
                }
            }

            private fun doPostJson(
                requestBody: String,
                timeoutSeconds: Int,
                session: String?,
            ): String {
                val safeTimeout = timeoutSeconds.coerceAtLeast(1)
                return HttpRequests.post(URL, "application/json")
                    .connectTimeout(safeTimeout * 1000)
                    .readTimeout(safeTimeout * 1000)
                    .accept("application/json, text/event-stream")
                    .tuner { connection ->
                        if (session != null) {
                            connection.setRequestProperty("mcp-session-id", session)
                        }
                    }
                    .connect { req ->
                        req.write(requestBody)
                        val responseText = req.readString()
                        val code = (req.connection as? HttpURLConnection)?.responseCode ?: -1
                        if (code < 200 || code >= 300) {
                            throw mcpExpectedError("Remote MCP HTTP error $code: ${responseText.take(2_000)}")
                        }
                        responseText
                    }
            }

            private fun ensureSessionId(timeoutSeconds: Int): String {
                val existing = sessionId
                if (!existing.isNullOrBlank()) return existing

                synchronized(RemoteMcpClient::class.java) {
                    val existing2 = sessionId
                    if (!existing2.isNullOrBlank()) return existing2

                    val requestBody = JSONObject()
                        .put("jsonrpc", "2.0")
                        .put("id", 1)
                        .put("method", "initialize")
                        .put(
                            "params",
                            JSONObject()
                                .put("protocolVersion", "2024-11-05")
                                .put("capabilities", JSONObject())
                                .put(
                                    "clientInfo",
                                    JSONObject()
                                        .put("name", "lsfusion-intellij")
                                        .put("version", "0")
                                )
                        )
                        .toString()

                    // For session initialization we need the `mcp-session-id` response header.
                    var headerSessionId: String? = null
                    val responseBody = HttpRequests.post(URL, "application/json")
                        .connectTimeout(timeoutSeconds.coerceAtLeast(1) * 1000)
                        .readTimeout(timeoutSeconds.coerceAtLeast(1) * 1000)
                        .accept("application/json, text/event-stream")
                        .connect { req ->
                            req.write(requestBody)
                            val responseText = req.readString()
                            val conn = req.connection as? HttpURLConnection
                            val code = conn?.responseCode ?: -1
                            if (code < 200 || code >= 300) {
                                throw mcpExpectedError("Remote MCP HTTP error $code: ${responseText.take(2_000)}")
                            }
                            headerSessionId = conn?.getHeaderField("mcp-session-id")
                            responseText
                        }

                    if (headerSessionId.isNullOrBlank()) {
                        throw mcpExpectedError("Remote MCP initialize did not return 'mcp-session-id'. Body: ${responseBody.take(2_000)}")
                    }

                    sessionId = headerSessionId
                    return headerSessionId!!
                }
            }

            internal fun callRemoteToolResultJson(
                toolName: String,
                arguments: JSONObject,
                timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS,
            ): JsonElement {
                val sid = ensureSessionId(timeoutSeconds)
                val requestBody = JSONObject()
                    .put("jsonrpc", "2.0")
                    .put("id", 1)
                    .put("method", "tools/call")
                    .put(
                        "params",
                        JSONObject()
                            .put("name", toolName)
                            .put("arguments", arguments)
                    )
                    .toString()

                val responseText = doPostJson(requestBody, timeoutSeconds, sid)
                val jsonText = extractJsonFromSseOrPlain(responseText)

                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val root = json.parseToJsonElement(jsonText)
                    val result = root.jsonObject["result"]
                        ?: throw IllegalStateException("Missing 'result' in remote response")
                    
                    val content = result.jsonObject["content"]?.jsonArray
                        ?: throw IllegalStateException("Missing 'content' in remote result")
                    
                    val text = content.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
                        ?: throw IllegalStateException("Empty or missing 'text' in remote content")
                    
                    return try {
                        json.parseToJsonElement(text)
                    } catch (e: Exception) {
                        // Return as raw string if it's not a JSON. 
                        // The caller should handle it or wrap it into a DTO.
                        if (text.startsWith("Error executing tool")) {
                            throw mcpExpectedError(text)
                        }
                        JsonPrimitive(text)
                    }
                } catch (e: McpExpectedError) {
                    throw e
                } catch (e: Exception) {
                    throw mcpExpectedError("Remote MCP tool '$toolName' call failed: ${e.message}")
                }
            }
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
    @McpDescription(description = "Find and inspect lsFusion elements in the IntelliJ project.")
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
            description = "Element name filter as CSV (comma-separated). Each item is either a word-search or a Java regex (Pattern.find)."
        )
        names: String? = null,
        @McpDescription(
            description = "Element code filter as CSV. Each item is either a word-search or a Java regex (Pattern.find)."
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
            if (names != null) query.put("name", names)
            if (contains != null) query.put("contains", contains)
            if (elementTypes != null) query.put("elementTypes", elementTypes)
            if (classes != null) query.put("classes", classes)
            if (relatedElements != null) query.put("relatedElements", relatedElements)
            if (relatedDirection != null) query.put("relatedDirection", relatedDirection)
            query.put("minSymbols", minSymbols)
            query.put("maxSymbols", maxSymbols)
            query.put("timeoutSeconds", timeoutSeconds)
            if (moreFilters != null && !moreFilters.isEmpty()) {
                query.put("moreFilters", JSONArray(moreFilters))
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
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (official documentation and language reference) — based on a single search query.")
    @Suppress("unused")
    suspend fun retrieveDocs(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = RemoteMcpClient.callRemoteToolResultJson("lsfusion_retrieve_docs", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_retrieve_howtos_and_samples")
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (code samples for combined tasks / scenarios and how-to) — based on a single search query.")
    @Suppress("unused")
    suspend fun retrieveHowtosAndSamples(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = RemoteMcpClient.callRemoteToolResultJson("lsfusion_retrieve_howtos_and_samples", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_retrieve_community")
    @McpDescription(description = "Fetch prioritized chunks from lsFusion RAG store (tutorials, articles, and community discussions) — based on a single search query. Use this ONLY for deep, ambiguous tasks when other retrieval tools (docs, howtos) did not provide a solution.")
    @Suppress("unused")
    suspend fun retrieveCommunity(
        @McpDescription(description = "Query")
        query: String,
    ): RetrieveDocsOutput {
        val resultEl = RemoteMcpClient.callRemoteToolResultJson("lsfusion_retrieve_community", JSONObject().put("query", query))
        return json.decodeFromJsonElement<RetrieveDocsOutput>(resultEl)
    }

    @McpTool(name = "lsfusion_validate_syntax")
    @McpDescription(description = "Validate the syntax of the list of lsFusion statements. Use this ONLY when IDE tools for error checking and code execution are not available.")
    @Suppress("unused")
    suspend fun validateSyntax(
        @McpDescription(description = "Text")
        text: String,
    ): DSLValidationResult {
        val resultEl = RemoteMcpClient.callRemoteToolResultJson("lsfusion_validate_syntax", JSONObject().put("text", text))
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
        val resultEl = RemoteMcpClient.callRemoteToolResultJson("lsfusion_get_guidance", JSONObject())
        return if (resultEl is JsonPrimitive) {
            resultEl.content
        } else {
            // Should not happen based on server code, but for robustness:
            resultEl.toString()
        }
    }
}
