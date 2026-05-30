package com.lsfusion.mcp

import com.intellij.mcpserver.McpExpectedError
import com.intellij.mcpserver.annotations.McpDescription
import com.intellij.mcpserver.annotations.McpTool
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.io.HttpRequests
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
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


// report_feedback DTOs — mirror mcp/tools/feedback.py (central validates authoritatively).
// Enum-valued fields are typed as String here (allowed values documented inline) to keep
// the JSON keys aligned via @SerialName without enum-name mapping.
@Serializable
data class FbExpectation(
    @McpDescription(description = "What the agent expected (depersonalized).") val expected: String,
    @McpDescription(description = "What actually happened (depersonalized).") val actual: String,
)

@Serializable
data class FbEvalError(
    @McpDescription(description = "The error text (depersonalized).") val message: String,
    @McpDescription(description = "Where it surfaced: syntax|semantic|runtime|unknown.") val phase: String = "unknown",
    @SerialName("code_excerpt")
    @McpDescription(description = "Tiny abstracted snippet if essential — NO full source / project code.") val codeExcerpt: String? = null,
    @SerialName("normalized_message")
    @McpDescription(description = "Optional normalized form (ids/literals stripped) for clustering.") val normalizedMessage: String? = null,
)

@Serializable
data class FbRetrieveQuery(
    @McpDescription(description = "The query text.") val query: String,
    @SerialName("returned_sources")
    @McpDescription(description = "Doc branches/files it surfaced, if noted.") val returnedSources: List<String>? = null,
    @McpDescription(description = "How useful: helpful|irrelevant|misleading|incomplete.") val usefulness: String? = null,
)

@Serializable
data class FbRecommendation(
    @SerialName("primary_target")
    @McpDescription(description = "Main artifact to change: how-to|rules|brief|paradigm|language|code-bug|rag-retrieval|eval-error-message.") val primaryTarget: String,
    @SerialName("secondary_targets")
    @McpDescription(description = "Other plausibly-affected artifacts (same value set as primary_target).") val secondaryTargets: List<String> = emptyList(),
    @SerialName("suggested_change")
    @McpDescription(description = "Concrete suggestion (depersonalized).") val suggestedChange: String,
    @McpDescription(description = "Confidence: low|medium|high.") val confidence: String = "medium",
    @McpDescription(description = "Why, briefly.") val rationale: String? = null,
)

@Serializable
data class FbToolContext(
    @SerialName("eval_server_kind")
    @McpDescription(description = "Eval server kind.") val evalServerKind: String? = null,
    @SerialName("eval_server_version")
    @McpDescription(description = "Eval server version.") val evalServerVersion: String? = null,
)

@Serializable
data class FeedbackReportInput(
    @SerialName("agent_journey_id")
    @McpDescription(description = "Agent-generated id grouping this task's errors/queries.") val agentJourneyId: String,
    @SerialName("signal_type")
    @McpDescription(description = "Signal kind: doc-gap|expectation-mismatch|unclear-error|missing-capability|rag-retrieval|other.") val signalType: String,
    @SerialName("problem_summary")
    @McpDescription(description = "Short depersonalized task description.") val problemSummary: String,
    @McpDescription(description = "Suggested fix (a hint for triage, not a decision).") val recommendation: FbRecommendation,
    @McpDescription(description = "For expectation-mismatch/unclear-error: expected vs actual.") val expectation: FbExpectation? = null,
    @SerialName("eval_errors")
    @McpDescription(description = "Errors hit while running lsFusion code via eval.") val evalErrors: List<FbEvalError> = emptyList(),
    @SerialName("retrieve_queries")
    @McpDescription(description = "retrieve_docs queries tried while fixing the error.") val retrieveQueries: List<FbRetrieveQuery> = emptyList(),
    @SerialName("retrieved_docs_summary")
    @McpDescription(description = "Short PUBLIC summaries of retrieved docs (no chunk bodies).") val retrievedDocsSummary: List<String> = emptyList(),
    @SerialName("final_outcome")
    @McpDescription(description = "How the task ended: fixed|not_fixed|workaround|abandoned|unknown.") val finalOutcome: String = "unknown",
    @SerialName("tool_context")
    @McpDescription(description = "Optional eval-server context.") val toolContext: FbToolContext? = null,
    @SerialName("client_dedup_hint")
    @McpDescription(description = "Optional agent hint; the SERVER computes the canonical dedup_fingerprint.") val clientDedupHint: String? = null,
    @SerialName("lsfusion_version")
    @McpDescription(description = "lsFusion version, if known.") val lsfusionVersion: String? = null,
    @SerialName("deployment_kind")
    @McpDescription(description = "Deployment kind, if known.") val deploymentKind: String? = null,
    @McpDescription(description = "Reporting client name/version, e.g. claude-code.") val agent: String? = null,
    @SerialName("n_eval_attempts")
    @McpDescription(description = "Number of eval attempts.") val nEvalAttempts: Int? = null,
)

@Serializable
data class FeedbackOutput(
    @SerialName("report_id")
    @McpDescription(description = "Server-assigned id for this submission.") val reportId: String,
    @McpDescription(description = "Outcome: recorded|disabled|rejected.") val status: String,
    @SerialName("dedup_fingerprint")
    @McpDescription(description = "Server-computed clustering fingerprint (when recorded).") val dedupFingerprint: String? = null,
    @McpDescription(description = "Reason when disabled/rejected.") val detail: String? = null,
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
    @McpDescription(description = "Search official lsFusion documentation (language, paradigm, how-to, brief, rules) for chunks relevant to a query. Returns `{docs:[{source,text,score}]}` sorted by descending score. Use `type` to narrow to one branch when known; omit to search all and merge. The corpus is English-only (`docs/en/`) — cross-lingual embeddings make non-English queries work, but English wording gives the best recall.")
    @Suppress("unused")
    suspend fun retrieveDocs(
        @McpDescription(description = "Short topical phrase. Semantic match (not literal); rephrase rather than retry the same query if results are weak.")
        query: String,
        @McpDescription(description = "Optional sourceType filter (the docs folder). Omit (or pass null) to search all branches and merge. `language` = syntax / operator reference; `paradigm` = concepts / abstractions; `how-to` = task recipes; `brief` = concise capability map; `rules` = code conventions.")
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

    @McpTool(name = "lsfusion_report_feedback")
    @McpDescription(
        description = "Submit ONE anonymous, depersonalized reinforcement-quality signal so lsFusion docs / RAG / eval diagnostics / the platform can be improved. " +
            "Use `signal_type` to say what kind: a documentation gap, an expectation-mismatch (you expected lsFusion to behave/mean X but it was actually Y — fill `expectation`), " +
            "an unclear/unactionable `eval` error, a missing capability, a RAG miss, or other. " +
            "Call this ONLY per the workflow rule from `lsfusion_get_guidance` (the friction was action-affecting) AND only after the user explicitly consents. " +
            "Send NO source code, file paths, schema/table/customer names, or secrets — only the depersonalized journey and a recommendation. The feedback is a suggestion, not a decision."
    )
    @Suppress("unused")
    suspend fun reportFeedback(
        @McpDescription(description = "The depersonalized feedback report. NO code, paths, schema/table/customer names, or secrets.")
        report: FeedbackReportInput,
    ): FeedbackOutput {
        val args = JSONObject().put("report", JSONObject(json.encodeToString(report)))
        val resultEl = callRemoteToolResultJson("lsfusion_report_feedback", args)
        return json.decodeFromJsonElement<FeedbackOutput>(resultEl)
    }
}
