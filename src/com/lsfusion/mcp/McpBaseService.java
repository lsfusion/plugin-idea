package com.lsfusion.mcp;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public abstract class McpBaseService extends RestService {

    protected static final String MCP_PROTOCOL_VERSION = "2024-11-05";
    protected static final String TOOL_NAME = "lsfusion_find_elements";

    protected static final String TOOL_RETRIEVE_DOCS = "lsfusion_retrieve_docs";
    protected static final String TOOL_RETRIEVE_HOWTOS = "lsfusion_retrieve_howtos";
    protected static final String TOOL_RETRIEVE_COMMUNITY = "lsfusion_retrieve_community";
    protected static final String TOOL_VALIDATE_SYNTAX = "lsfusion_validate_syntax";
    protected static final String TOOL_GET_GUIDANCE = "lsfusion_get_guidance";

    private static final String TOOL_RETRIEVE_DOCS_DESCRIPTION = "Fetch prioritized chunks from lsFusion RAG store (official documentation and language reference) — based on a single search query.";
    private static final String TOOL_RETRIEVE_HOWTOS_DESCRIPTION = "Fetch prioritized chunks from lsFusion RAG store (examples for combined tasks / scenarios and how-tos) — based on a single search query.";
    private static final String TOOL_RETRIEVE_COMMUNITY_DESCRIPTION = "Fetch prioritized chunks from lsFusion RAG store (tutorials, articles, and community discussions) — based on a single search query. Use this ONLY for deep, ambiguous tasks when other retrieval tools (docs, howtos) did not provide a solution.";

    private static final Set<String> REMOTE_TOOL_NAMES = Set.of(
            TOOL_RETRIEVE_DOCS,
            TOOL_RETRIEVE_HOWTOS,
            TOOL_RETRIEVE_COMMUNITY,
            TOOL_VALIDATE_SYNTAX,
            TOOL_GET_GUIDANCE
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
                .put(buildQueryToolDescriptor(TOOL_RETRIEVE_DOCS, TOOL_RETRIEVE_DOCS_DESCRIPTION))
                .put(buildQueryToolDescriptor(TOOL_RETRIEVE_HOWTOS, TOOL_RETRIEVE_HOWTOS_DESCRIPTION))
                .put(buildQueryToolDescriptor(TOOL_RETRIEVE_COMMUNITY, TOOL_RETRIEVE_COMMUNITY_DESCRIPTION))
                .put(buildValidateSyntaxToolDescriptor())
                .put(buildGetGuidanceToolDescriptor());
    }

    protected static JSONObject buildQueryToolDescriptor(String name, String description) {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("query", new JSONObject()
                                .put("type", "string")
                                .put("description", "Query")))
                .put("required", new JSONArray().put("query"))
                .put("additionalProperties", false);

        // Output schema matches RetrieveDocsOutput in McpToolset.kt
        JSONObject docItemSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("source", new JSONObject().put("type", "string").put("description", "Chunk origin (e.g. docs, howto, tutorial)."))
                        .put("text", new JSONObject().put("type", "string").put("description", "Retrieved text snippet."))
                        .put("score", new JSONObject().put("type", "number").put("description", "Similarity score (higher = more relevant).")))
                .put("required", new JSONArray().put("source").put("text").put("score"));


        // Output schema matches RetrieveDocsOutput in McpToolset.kt
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
                .put("name", name)
                .put("description", description)
                .put("inputSchema", inputSchema)
                .put("outputSchema", outputSchema);
    }

    protected static JSONObject buildValidateSyntaxToolDescriptor() {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "string")
                                .put("description", "Text")))
                .put("required", new JSONArray().put("text"))
                .put("additionalProperties", false);

        // Output schema matches DSLValidationResult in McpToolset.kt
        JSONObject dslErrorSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("type", new JSONObject().put("type", "string").put("description", "Error category: 'syntax' or 'semantic'."))
                        .put("message", new JSONObject().put("type", "string").put("description", "Readable explanation of the problem."))
                        .put("line", new JSONObject().put("type", "integer").put("description", "Line number (1-based) where error starts."))
                        .put("column", new JSONObject().put("type", "integer").put("description", "Column number (0-based) where error starts."))
                        .put("found", new JSONObject().put("type", "string").put("description", "Actual token or fragment found, if any."))
                        .put("expected", new JSONObject().put("type", "array").put("items", new JSONObject().put("type", "string")).put("description", "List of tokens or constructs expected at this point."))
                        .put("hint", new JSONObject().put("type", "string").put("description", "Short fix suggestion to resolve the issue."))
                        .put("excerpt", new JSONObject().put("type", "string").put("description", "Line of source where the error occurred."))
                        .put("pointer", new JSONObject().put("type", "string").put("description", "Caret marker showing error position in the excerpt.")))
                .put("required", new JSONArray().put("type").put("message").put("line").put("column"));

        JSONObject outputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("ok", new JSONObject()
                                .put("type", "boolean")
                                .put("description", "True if DSL text parsed successfully without errors."))
                        .put("errors", new JSONObject()
                                .put("type", "array")
                                .put("items", dslErrorSchema)
                                .put("description", "Detailed error entries (empty if ok=True).")))
                .put("required", new JSONArray().put("ok").put("errors"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_VALIDATE_SYNTAX)
                .put("description", "Validate the syntax of the list of lsFusion statements. Use this ONLY when IDE tools for error checking and code execution are not available.")
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
                .put("description", "Fetch the brief overview and mandatory rules for working with lsFusion. IMPORTANT: The assistant MUST call this tool before ANY task related to lsFusion if it's not already in your context. The assistant MUST read and strictly follow all rules and guidelines provided by this tool for ANY lsFusion-related task.")
                .put("inputSchema", inputSchema);
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
        ByteBuf content = request.content();
        if (!content.isReadable()) {
            return new JSONObject();
        }
        try (InputStream is = new ByteBufInputStream(content)) {
            byte[] bytes = is.readAllBytes();
            String s = new String(bytes, StandardCharsets.UTF_8).trim();
            if (s.isEmpty()) {
                return new JSONObject();
            }
            return new JSONObject(s);
        }
    }

    protected void sendJsonResponse(@NotNull ChannelHandlerContext ctx,
                                    @NotNull FullHttpRequest req,
                                    @NotNull JSONObject json) {
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                buf
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

        sendResponse(req, ctx, response);
    }
}