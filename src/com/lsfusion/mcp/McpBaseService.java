package com.lsfusion.mcp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.BufferExposingByteArrayOutputStream;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.util.LSFFileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public abstract class McpBaseService extends RestService {

    protected static final String MCP_PROTOCOL_VERSION = "2024-11-05";
    protected static final String TOOL_NAME = "lsfusion_find_elements";
    protected static final String TOOL_SET_META_VISIBILITY = "lsfusion_set_meta_visibility";

    protected static final String TOOL_RETRIEVE_DOCS = "lsfusion_retrieve_docs";
    protected static final String TOOL_GET_GUIDANCE = "lsfusion_get_guidance";
    protected static final String TOOL_REPORT_FEEDBACK = "lsfusion_report_feedback";

    private static final String TOOL_RETRIEVE_DOCS_DESCRIPTION = "Search official lsFusion documentation for chunks relevant to a query. Returns `{docs:[{id,source,text,score}]}` sorted by descending score. Use `type` to narrow to one branch when known; omit to search all five and merge (the two top guidance articles are always excluded — get_guidance serves those in full). To page deeper on one information need, pass the `id` values you already hold in `exclude_ids`; they are filtered out before ranking. Omit them when rephrasing for a better ranking or asking a different question, or the filter will drop the chunk that best answers it. The corpus is English-only (`docs/en/`) — cross-lingual embeddings make non-English queries work, but English wording gives the best recall.";

    // Sister tools lsfusion_retrieve_howtos / lsfusion_retrieve_community were
    // removed together with the legacy Pinecone backend that fed them; the new
    // OpenAI VS indexes all five doc folders (language, paradigm, how-to, brief,
    // rules) under the single lsfusion_retrieve_docs tool.
    private static final Set<String> REMOTE_TOOL_NAMES = Set.of(
            TOOL_RETRIEVE_DOCS,
            TOOL_GET_GUIDANCE,
            TOOL_REPORT_FEEDBACK
    );

    private static final int FALLBACK_ID = 0;

    protected abstract Logger getLogger();

    protected String getRpcHandlerName() {
        return "MCP handler";
    }

    protected abstract boolean useFallbackId();

    protected JSONObject handleInitialize(String jsonrpc, Object id, @Nullable JSONObject params) {
        JSONObject result = new JSONObject()
                .put("protocolVersion", MCP_PROTOCOL_VERSION)
                .put("serverInfo", new JSONObject()
                        .put("name", "lsfusion-intellij")
                        .put("version", "1.0.0"))
                .put("capabilities", new JSONObject()
                        .put("tools", new JSONObject().put("listChanged", false))
                        .put("resources", new JSONObject().put("listChanged", false))
                        .put("prompts", new JSONObject().put("listChanged", false)));
        return rpcResult(jsonrpc, id, result);
    }

    protected JSONObject handleToolsList(String jsonrpc, Object id, @Nullable JSONObject params) {
        return rpcResult(jsonrpc, id, new JSONObject().put("tools", buildToolsList()));
    }

    protected JSONObject handleToolsCall(@NotNull Project project,
                                         String jsonrpc,
                                         Object id,
                                         @Nullable JSONObject params) {
        if (params == null) {
            return rpcError(jsonrpc, id, -32602, "Missing params for tools/call", null);
        }

        String name = params.optString("name", params.optString("toolName", null));
        JSONObject arguments = params.optJSONObject("arguments");
        if (arguments == null) {
            arguments = new JSONObject();
        }

        if (isRemoteToolName(name)) {
            return handleRemoteToolCall(jsonrpc, id, name, arguments);
        }

        if (TOOL_SET_META_VISIBILITY.equals(name)) {
            return handleSetMetaVisibilityToolCall(project, jsonrpc, id, arguments);
        }

        if (!TOOL_NAME.equals(name)) {
            return rpcError(jsonrpc, id, -32601, "Unknown tool: " + name, null);
        }

        return handleFindElementsToolCall(project, jsonrpc, id, arguments);
    }

    protected JSONObject handleRpc(@NotNull Project project, @NotNull JSONObject rpc) {
        String jsonrpc = rpc.optString("jsonrpc", "2.0");
        Object id = rpc.opt("id");
        String method = rpc.optString("method", null);
        JSONObject params = rpc.optJSONObject("params");

        if (!"2.0".equals(jsonrpc)) {
            return rpcError(jsonrpc, id, -32600, "Invalid jsonrpc version, expected 2.0", null);
        }
        if (method == null) {
            return rpcError(jsonrpc, id, -32600, "Missing method", null);
        }

        try {
            return switch (method) {
                case "initialize" -> handleInitialize(jsonrpc, id, params);
                case "tools/list" -> handleToolsList(jsonrpc, id, params);
                case "tools/call" -> handleToolsCall(project, jsonrpc, id, params);
                default -> rpcError(jsonrpc, id, -32601, "Method not found: " + method, null);
            };
        } catch (Exception e) {
            getLogger().warn("Exception in " + getRpcHandlerName(), e);
            JSONObject data = new JSONObject()
                    .put("exception", e.getClass().getName())
                    .put("message", String.valueOf(e.getMessage()));
            return rpcError(jsonrpc, id, -32000, "Internal MCP server error", data);
        }
    }

    protected JSONObject rpcResult(String jsonrpc, Object id, JSONObject result) {
        JSONObject resp = new JSONObject().put("jsonrpc", jsonrpc);
        if (useFallbackId()) {
            resp.put("id", id != null && id != JSONObject.NULL ? id : FALLBACK_ID);
        } else if (id != null) {
            resp.put("id", id);
        }
        resp.put("result", result);
        return resp;
    }

    protected JSONObject rpcError(String jsonrpc,
                                  Object id,
                                  int code,
                                  String message,
                                  @Nullable JSONObject data) {
        JSONObject error = new JSONObject().put("code", code).put("message", message);
        if (data != null) {
            error.put("data", data);
        }
        JSONObject resp = new JSONObject().put("jsonrpc", jsonrpc);
        if (useFallbackId()) {
            resp.put("id", id != null && id != JSONObject.NULL ? id : FALLBACK_ID);
        } else if (id != null) {
            resp.put("id", id);
        }
        resp.put("error", error);
        return resp;
    }

    protected JSONObject handleRemoteToolCall(String jsonrpc, Object id, String toolName, JSONObject arguments) {
        try {
            String payload = RemoteMcpClient.callRemoteTool(toolName, arguments);

            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(new JSONObject()
                            .put("type", "text")
                            .put("text", payload)))
                    .put("isError", false);

            if (!TOOL_GET_GUIDANCE.equals(toolName)) {
                // Most remote tools (except get_guidance for now) return JSON object; try parse and attach structuredContent
                try {
                    callResult.put("structuredContent", new JSONObject(payload));
                } catch (Exception e1) {
                    // Some tools could return JSON arrays; try that too
                    try {
                        callResult.put("structuredContent", new JSONArray(payload));
                    } catch (Exception ignored) {
                        callResult.put("structuredContent", payload);
                    }
                }
            }

            return rpcResult(jsonrpc, id, callResult);
        } catch (Exception e) {
            getLogger().warn(toolName + " failed", e);
            return rpcResult(jsonrpc, id, buildToolCallErrorResult(toolName, e.getMessage()));
        }
    }

    protected JSONObject handleFindElementsToolCall(@NotNull Project project,
                                                    String jsonrpc,
                                                    Object id,
                                                    @NotNull JSONObject arguments) {
        try {
            JSONObject payloadObj = MCPSearchUtils.findElements(project, arguments);
            return rpcResult(jsonrpc, id, buildLocalToolCallResult(payloadObj));
        } catch (Exception e) {
            getLogger().warn(TOOL_NAME + " failed", e);
            return rpcResult(jsonrpc, id, buildToolCallErrorResult(TOOL_NAME, e.getMessage()));
        }
    }

    protected JSONObject handleSetMetaVisibilityToolCall(@NotNull Project project,
                                                         String jsonrpc,
                                                         Object id,
                                                         @NotNull JSONObject arguments) {
        try {
            String path = arguments.optString("path", null);
            String action = arguments.optString("action", null);
            if (path == null || path.isEmpty()) {
                return rpcResult(jsonrpc, id, buildToolCallErrorResult(TOOL_SET_META_VISIBILITY, "Missing required argument: path"));
            }
            boolean show;
            if ("show".equals(action)) {
                show = true;
            } else if ("hide".equals(action)) {
                show = false;
            } else {
                return rpcResult(jsonrpc, id, buildToolCallErrorResult(TOOL_SET_META_VISIBILITY, "`action` must be `show` or `hide`, got: " + action));
            }

            LSFFile file = LSFFileUtils.findLsfFile(project, path);
            if (file == null) {
                return rpcResult(jsonrpc, id, buildToolCallErrorResult(TOOL_SET_META_VISIBILITY, "Not an lsFusion file, or file not found in the project: " + path));
            }

            MetaChangeDetector.getInstance(project).reprocessFileForMcp(file, show);

            JSONObject payloadObj = new JSONObject().put("status", show ? "shown" : "hidden").put("path", path);
            return rpcResult(jsonrpc, id, buildLocalToolCallResult(payloadObj));
        } catch (Exception e) {
            getLogger().warn(TOOL_SET_META_VISIBILITY + " failed", e);
            return rpcResult(jsonrpc, id, buildToolCallErrorResult(TOOL_SET_META_VISIBILITY, e.getMessage()));
        }
    }

    protected JSONObject buildLocalToolCallResult(@NotNull JSONObject payloadObj) {
        return new JSONObject()
                .put("content", new JSONArray().put(new JSONObject()
                        .put("type", "text")
                        .put("text", payloadObj.toString())))
                .put("structuredContent", payloadObj)
                .put("isError", false);
    }

    protected JSONObject buildToolCallErrorResult(@NotNull String toolName, @Nullable String message) {
        return new JSONObject()
                .put("content", new JSONArray().put(new JSONObject()
                        .put("type", "text")
                        .put("text", toolName + " error: " + message)))
                .put("isError", true);
    }

    protected boolean isRemoteToolName(@Nullable String name) {
        return name != null && REMOTE_TOOL_NAMES.contains(name);
    }

    protected JSONArray buildToolsList() {
        return new JSONArray()
                .put(buildFindElementsToolDescriptor())
                .put(buildSetMetaVisibilityToolDescriptor())
                .put(buildRetrieveDocsToolDescriptor())
                .put(buildGetGuidanceToolDescriptor())
                .put(buildReportFeedbackToolDescriptor());
    }

    private static final String SET_META_VISIBILITY_DESCRIPTION =
            "Show (expand) or hide (collapse) generated `META` code bodies for ONE lsFusion file, without touching other files or the project-wide meta-code toggle. " +
            "`hide` collapses `@name(args){ ... }` usages back to bare `@name(args);` (the form that must be committed - a raised code-review rule rejects commits with expanded meta bodies). " +
            "`show` re-expands previously collapsed usages back to their generated body (e.g. to inspect or edit the generated code). " +
            "Works regardless of whether the file is currently open in the editor, and saves the file to disk before returning, so a `hide` call is immediately safe to commit.";

    protected static JSONObject buildSetMetaVisibilityToolDescriptor() {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("path", new JSONObject()
                                .put("type", "string")
                                .put("description", "Absolute path, or path relative to the project root, of the `.lsf` file."))
                        .put("action", new JSONObject()
                                .put("type", "string")
                                .put("enum", new JSONArray().put("show").put("hide"))
                                .put("description", "`show` to expand META bodies inline; `hide` to collapse them back to bare declarations.")))
                .put("required", new JSONArray().put("path").put("action"))
                .put("additionalProperties", false);

        JSONObject outputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("status", new JSONObject().put("type", "string").put("enum", new JSONArray().put("shown").put("hidden")))
                        .put("path", new JSONObject().put("type", "string")))
                .put("required", new JSONArray().put("status").put("path"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_SET_META_VISIBILITY)
                .put("description", SET_META_VISIBILITY_DESCRIPTION)
                .put("inputSchema", inputSchema)
                .put("outputSchema", outputSchema);
    }

    private static JSONObject buildRetrieveDocsToolDescriptor() {
        JSONObject typeProp = new JSONObject()
                // anyOf, not a bare enum: the description offers `null` as a way to
                // say "search everything", and a strict client validating against a
                // string-only enum would reject exactly that.
                .put("anyOf", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "string")
                                .put("enum", new JSONArray().put("language").put("paradigm").put("how-to").put("brief").put("rules")))
                        .put(new JSONObject().put("type", "null")))
                .put("description", "Optional sourceType filter (the docs folder). Omit (or pass null) to search all five branches and merge; only the two TOP articles (`Brief`, `Rules`) are excluded, because get_guidance already delivers them in full. `language` = syntax / operator reference; `paradigm` = concepts / abstractions; `how-to` = task recipes; `brief` = concise capability map; `rules` = coding constraints for one area — unlike the other branches this lookup is not optional: perform it before working in an area.");
        JSONObject excludeIdsProp = new JSONObject()
                .put("type", "array")
                .put("items", new JSONObject().put("type", "string"))
                .put("description", "Chunk `id` values you already hold. They are excluded server-side BEFORE ranking, so the quota is spent on material you do not have. Use this to page deeper on the same information need. Do NOT use it to rephrase a query for a better ranking, or to ask a different question about the same area: the filter ignores the new query, so a chunk that is now the most relevant one would be dropped before ranking. Leave empty on the first call.");
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        // string OR array: one call may carry several independent
                        // needs, which is the normal shape of real traffic.
                        .put("query", new JSONObject()
                                .put("anyOf", new JSONArray()
                                        .put(new JSONObject().put("type", "string"))
                                        .put(new JSONObject().put("type", "array")
                                                .put("items", new JSONObject().put("type", "string"))))
                                .put("description", "One short technical query, or a list of DISTINCT queries for independent needs already known before this call. Batch only lookups that do not depend on one another; when one answer can determine or refine the next query, call the tool again instead. Do not batch alternative phrasings of one need. In a batch, `type` and `exclude_ids` apply to every query, all queries share one result cap, a chunk answering two of them is returned once, and each result names the query it is credited to. Semantic match (not literal); rephrase rather than retry the same query if results are weak."))
                        .put("type", typeProp)
                        .put("exclude_ids", excludeIdsProp))
                .put("required", new JSONArray().put("query"))
                .put("additionalProperties", false);

        // Output schema matches RetrieveDocsOutput in McpToolset.kt
        JSONObject docItemSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        // `id` is deliberately NOT required — it mirrors the nullable `RemoteDocItem.id`,
                        // so a response from an older server that does not send it still decodes.
                        .put("id", new JSONObject().put("type", "string").put("description", "Stable chunk id; pass the ids you already received back in `exclude_ids` to avoid getting the same chunks again."))
                        .put("source", new JSONObject().put("type", "string").put("description", "Chunk origin (e.g. documentation-language, documentation-paradigm)."))
                        .put("text", new JSONObject().put("type", "string").put("description", "Retrieved text snippet."))
                        .put("score", new JSONObject().put("type", "number").put("description", "Similarity score (higher = more relevant)."))
                        // Like `id`, deliberately not required: an older server does not send it.
                        .put("query", new JSONObject().put("type", "string").put("description", "Which of the submitted queries this chunk answers; null when only one was submitted.")))
                .put("required", new JSONArray().put("source").put("text").put("score"));

        JSONObject outputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("docs", new JSONObject()
                                .put("type", "array")
                                .put("items", docItemSchema)
                                .put("description", "Relevant chunks returned from the RAG store.")))
                .put("required", new JSONArray().put("docs"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_RETRIEVE_DOCS)
                .put("description", TOOL_RETRIEVE_DOCS_DESCRIPTION)
                .put("inputSchema", inputSchema)
                .put("outputSchema", outputSchema);
    }

    protected static JSONObject buildGetGuidanceToolDescriptor() {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject())
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_GET_GUIDANCE)
                .put("description", "Fetch the brief overview and the CORE rules for working with lsFusion. The assistant MUST call this at the start of ANY lsFusion-related task — writing, modifying or reviewing lsFusion code, or answering questions about its syntax or semantics — and MUST then read what it returns and apply each rule according to that rule's stated strength (MUST / MUST NOT are binding; SHOULD / SHOULD NOT are recommendations). Once per session is enough. This is the top level only: the rules for a specific area are separate articles, retrieved with `lsfusion_retrieve_docs(type='rules')`.")
                .put("inputSchema", inputSchema);
    }

    // --- report_feedback schema helpers (mirror mcp/tools/feedback.py; central validates) ---
    private static JSONArray feArr(String... values) {
        JSONArray a = new JSONArray();
        for (String v : values) a.put(v);
        return a;
    }

    private static JSONObject feStr(String description) {
        return new JSONObject().put("type", "string").put("description", description);
    }

    private static JSONObject feEnum(String description, JSONArray values) {
        return new JSONObject().put("type", "string").put("enum", values).put("description", description);
    }

    private static JSONObject feArrOf(JSONObject items, String description) {
        return new JSONObject().put("type", "array").put("items", items).put("description", description);
    }

    private static JSONObject buildReportFeedbackToolDescriptor() {
        JSONArray targets = feArr("how-to", "rules", "brief", "paradigm", "language",
                "code-bug", "rag-retrieval", "eval-error-message");
        JSONObject recommendation = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("primary_target", feEnum("Main artifact to change.", targets))
                        .put("secondary_targets", feArrOf(feEnum("", targets), "Other plausibly-affected artifacts."))
                        .put("suggested_change", feStr("Concrete suggestion (depersonalized)."))
                        .put("confidence", feEnum("How confident the agent is.", feArr("low", "medium", "high")))
                        .put("rationale", feStr("Why, briefly.")))
                .put("required", feArr("primary_target", "suggested_change"));
        JSONObject expectation = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("expected", feStr("What the agent expected (depersonalized)."))
                        .put("actual", feStr("What actually happened (depersonalized).")))
                .put("required", feArr("expected", "actual"));
        JSONObject evalError = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("message", feStr("The error text (depersonalized)."))
                        .put("phase", feEnum("Where it surfaced.", feArr("syntax", "semantic", "runtime", "unknown")))
                        .put("code_excerpt", feStr("Tiny abstracted snippet if essential — NO full source / project code."))
                        .put("normalized_message", feStr("Optional normalized form (ids/literals stripped) for clustering.")))
                .put("required", feArr("message"));
        JSONObject retrieveQuery = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("query", feStr("The query text."))
                        .put("returned_sources", feArrOf(new JSONObject().put("type", "string"), "Doc branches/files it surfaced, if noted."))
                        .put("usefulness", feEnum("How useful the result was for this error.", feArr("helpful", "irrelevant", "misleading", "incomplete"))))
                .put("required", feArr("query"));
        JSONObject toolContext = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("eval_server_kind", feStr("Eval server kind."))
                        .put("eval_server_version", feStr("Eval server version.")));
        JSONObject report = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("agent_journey_id", feStr("Agent-generated id grouping this task's errors/queries."))
                        .put("signal_type", feEnum("What kind of reinforcement signal this is (routing hint, not the decision).",
                                feArr("doc-gap", "expectation-mismatch", "unclear-error", "missing-capability", "rag-retrieval", "other")))
                        .put("problem_summary", feStr("Short depersonalized task description."))
                        .put("recommendation", recommendation)
                        .put("expectation", expectation)
                        .put("eval_errors", feArrOf(evalError, "Errors hit while running lsFusion code via eval."))
                        .put("retrieve_queries", feArrOf(retrieveQuery, "retrieve_docs queries tried while fixing the error."))
                        .put("retrieved_docs_summary", feArrOf(new JSONObject().put("type", "string"), "Short PUBLIC summaries of retrieved docs (no chunk bodies)."))
                        .put("final_outcome", feEnum("How the task ended (guards against survivorship bias).",
                                feArr("fixed", "not_fixed", "workaround", "abandoned", "unknown")))
                        .put("tool_context", toolContext)
                        .put("client_dedup_hint", feStr("Optional agent hint; the SERVER computes the canonical dedup_fingerprint."))
                        .put("lsfusion_version", feStr("lsFusion version, if known."))
                        .put("deployment_kind", feStr("Deployment kind, if known."))
                        .put("agent", feStr("Reporting client name/version, e.g. claude-code."))
                        .put("n_eval_attempts", new JSONObject().put("type", "integer").put("description", "Number of eval attempts.")))
                .put("required", feArr("agent_journey_id", "signal_type", "problem_summary", "recommendation"));
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject().put("report", report))
                .put("required", feArr("report"))
                .put("additionalProperties", false);

        // Output schema matches FeedbackOutput in McpToolset.kt
        JSONObject outputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("report_id", feStr("Server-assigned id for this submission."))
                        .put("status", feEnum("Outcome.", feArr("recorded", "disabled", "rejected")))
                        .put("dedup_fingerprint", feStr("Server-computed clustering fingerprint (when recorded)."))
                        .put("detail", feStr("Reason when disabled/rejected.")))
                .put("required", feArr("report_id", "status"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_REPORT_FEEDBACK)
                .put("description",
                        "Submit ONE anonymous, depersonalized reinforcement-quality signal so lsFusion docs / RAG / eval diagnostics / the platform can be improved. Use `signal_type` to say what kind: a documentation gap, an expectation-mismatch (you expected lsFusion to behave/mean X but it was actually Y — fill `expectation`), an unclear/unactionable `eval` error, a missing capability, a RAG miss, or other. Call this ONLY per the workflow rule from `lsfusion_get_guidance` (the friction was action-affecting) AND only after the user explicitly consents. Send NO source code, file paths, schema/table/customer names, or secrets — only the depersonalized journey and a recommendation. The feedback is a suggestion, not a decision. Returns `{report_id, status, dedup_fingerprint}`.")
                .put("inputSchema", inputSchema)
                .put("outputSchema", outputSchema);
    }

    protected static JSONObject buildFindElementsToolDescriptor() {
        // Base schema for a single (root) filter object (without `moreFilters` to avoid recursive schema).
        JSONObject filterSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("modules", new JSONObject()
                                .put("type", "string")
                                .put("description", "Module filter as CSV (comma-separated), e.g. `ModuleA, ModuleB`."))
                        .put("requiredModules", new JSONObject()
                                .put("type", "boolean")
                                .put("default", true)
                                .put("description", "Include required lsFusion modules for specified lsFusion modules (only if 'modules' is provided). Default: true."))
                        .put("scope", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Scope filter (IDEA concept): omitted = project + libraries; `project` = project content only; otherwise, a CSV list of IDEA module names."))
                        .put("names", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Element name filter as CSV (comma-separated). Word if valid ID, else Java regex."))
                        .put("contains", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Element code filter as CSV. Word if valid ID, else Java regex."))
                        .put("elementTypes", new JSONObject()
                                .put("type", "string")
                                .put("description", "Element type filter as CSV. Allowed values: `module`, `metacode`, `class`, `property`, `action`, `form`, `navigatorElement`, `window`, `group`, `table`, `event`, `calculatedEvent`, `constraint`, `index`."))
                        .put("classes", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Class filter as CSV (with namespace `MyNS.MyClass` or without `MyClass`). For property/action: matches parameter classes (best-effort)."))
                        .put("relatedElements", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Related elements filter (usage-graph traversal seeds) as CSV. Each item is either `type:name` (named element) or `location` (unnamed element). `location` format: `<module>(<line>:<col>)`, 1-based, e.g. `MyModule(10:5)`."))
                        .put("relatedDirection", new JSONObject()
                                .put("type", "string")
                                .put("default", "both")
                                .put("enum", new JSONArray().put("both").put("uses").put("used"))
                                .put("description",
                                        "Direction for ALL `relatedElements` seeds. Allowed values: `both`, `uses`, `used`. Default: `both`."))
                )
                .put("additionalProperties", false);

        // Allow nesting `moreFilters` only at the root level.
        JSONObject rootWithMoreSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject(filterSchema.getJSONObject("properties").toMap())
                        .put("moreFilters", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Additional filter objects of the same structure as the root. JSON array string (e.g. `[{\"names\":\"Foo\", \"modules\" : \"MyModule\"},{\"names\":\"Bar\"}]`). Results are merged (OR)."))
                        .put("minSymbols", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 0)
                                .put("default", MCPSearchUtils.DEFAULT_MIN_SYMBOLS)
                                .put("description",
                                        "Best-effort minimum output size in JSON chars; server may append neighboring elements if too small (>= 0). Default: " + MCPSearchUtils.DEFAULT_MIN_SYMBOLS + "."))
                        .put("maxSymbols", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 1)
                                .put("default", MCPSearchUtils.DEFAULT_MAX_SYMBOLS)
                                .put("description", "Hard cap for total output size in JSON chars (>= 1). Default: " + MCPSearchUtils.DEFAULT_MAX_SYMBOLS + "."))
                        .put("timeoutSeconds", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 1)
                                .put("default", MCPSearchUtils.DEFAULT_TIMEOUT_SECS)
                                .put("description", "Best-effort wall-clock timeout in seconds (>= 1). Default: " + MCPSearchUtils.DEFAULT_TIMEOUT_SECS + ".")))
                .put("additionalProperties", false);

        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("title", "lsFusion code search query")
                .put("description",
                        "Search lsFusion elements using AND between the provided fields at the root level. " +
                                "Use `moreFilters` for OR (merge). If no filter fields are provided, search runs without filters.")
                .put("properties", rootWithMoreSchema.getJSONObject("properties"))
                .put("additionalProperties", false);

        // Output schema matches FindElementsResult / FindElementItem in McpToolset.kt
        JSONObject codePartSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("location", new JSONObject()
                                .put("type", "string")
                                .put("description", "Element location in source. Format: `<module>(<line>:<col>)` (1-based), e.g. `MyModule(10:5)`."))
                        .put("code", new JSONObject()
                                .put("type", "string")
                                .put("description", "Element source code snippet. May be shortened depending on output size limits."))
                        .put("metacodeStack", new JSONObject()
                                .put("type", "array")
                                .put("items", new JSONObject().put("type", "string"))
                                .put("description", "Stack of enclosing `META` blocks (headers), from nearest to farthest. Omitted when element is not inside `META`.")));

        JSONObject itemSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("type", new JSONObject()
                                .put("type", "string")
                                .put("description", "Element type, e.g. `property`, `class`, `action`, ..."))
                        .put("name", new JSONObject()
                                .put("type", "string")
                                .put("description", "Canonical element name (best-effort). Omitted for unnamed statements."))
                        .put("moreNames", new JSONObject()
                                .put("type", "array")
                                .put("items", new JSONObject().put("type", "string"))
                                .put("description", "Additional canonical names for the same declaration (e.g. from `EXTEND`)."))
                        .put("location", new JSONObject()
                                .put("type", "string")
                                .put("description", "Location of the main statement. Format: `<module>(<line>:<col>)` (1-based)."))
                        .put("code", new JSONObject()
                                .put("type", "string")
                                .put("description", "Main statement code snippet. May be shortened depending on output size limits."))
                        .put("metacodeStack", new JSONObject()
                                .put("type", "array")
                                .put("items", new JSONObject().put("type", "string"))
                                .put("description", "Stack of enclosing `META` blocks for the main statement (headers), from nearest to farthest."))
                        .put("extends", new JSONObject()
                                .put("type", "array")
                                .put("items", codePartSchema)
                                .put("description", "Code fragments for `EXTEND`-ed declarations of the same element. Each entry contains its own `location`, `code`, and `metacodeStack`.")));

        JSONObject outputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("items", new JSONObject()
                                .put("type", "array")
                                .put("items", itemSchema)
                                .put("description", "Found elements (each item represents one declaration plus optional `EXTEND` fragments)."))
                        .put("meta", new JSONObject()
                                .put("type", "string")
                                .put("description", "Optional single-line meta reason (e.g. `too long - timeout hit`, `too large - max symbols hit`, `too small - non matching elements added`)."))
                        .put("errors", new JSONObject()
                                .put("type", "array")
                                .put("items", new JSONObject().put("type", "string"))
                                .put("description", "List of errors encountered during search (e.g. timeouts, internal errors).")))
                .put("required", new JSONArray().put("items"));

        return new JSONObject()
                .put("name", TOOL_NAME)
                .put("description",
                        "Find and inspect lsFusion elements. Results are prioritized (modules/classes > properties > actions > forms > others) and automatically truncated to a 'brief' (keeping key parts) to fit maxSymbols. If elements cannot be found (e.g. by name), search with minimal filters to explore.")
                .put("inputSchema", inputSchema)
                .put("outputSchema", outputSchema);
    }

    protected static JSONObject readJsonBody(@NotNull FullHttpRequest request) throws Exception {
        // createJsonReader() wraps the request content for us, avoiding the platform-internal io.netty.buffer API.
        try (JsonReader reader = createJsonReader(request)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element == null || element.isJsonNull()) { // empty body
                return new JSONObject();
            }
            return new JSONObject(element.toString());
        }
    }

    protected void sendJsonResponse(@NotNull ChannelHandlerContext ctx,
                                    @NotNull FullHttpRequest req,
                                    @NotNull JSONObject json) {
        // send() builds an application/json response (with Content-Length) without us touching io.netty.buffer.
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        BufferExposingByteArrayOutputStream out = new BufferExposingByteArrayOutputStream();
        out.write(bytes, 0, bytes.length);
        send(out, req, ctx);
    }
}