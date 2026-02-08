package com.lsfusion.mcp;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * MCP Server implementation for lsFusion IntelliJ plugin.
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
 *    - Update/add a corresponding tool descriptor in `handleToolsList`.
 *    - Update/add logic in `handleToolsCall` and constants for tool names.
 *    - Keep parsing tolerant because remote schemas can evolve.
 * 4) Validate manually by running IDEA MCP `tools/list` and checking that parameter names and
 *    input/output schemas match expectations.
 */
public final class McpServerService extends RestService {

    public McpServerService() {
    }

    private static final Logger LOG = Logger.getInstance(McpServerService.class);

    private static final String MCP_PROTOCOL_VERSION = "2024-11-05";
    private static final String TOOL_NAME = "lsfusion_find_elements";

    @NotNull
    @Override
    public String getServiceName() {
        return "sse-lsf";
    }

    @Override
    protected boolean isPrefixlessAllowed() {
        return true;
    }

    @Override
    protected boolean isMethodSupported(@NotNull HttpMethod method) {
        // SSE тоже будем принимать по POST — так не надо городить отдельный URL/handler
        return HttpMethod.POST.equals(method);
    }

    @Override
    public boolean isAccessible(@NotNull HttpRequest request) {
        return true;
    }

    @Override
    public @Nullable String execute(@NotNull QueryStringDecoder urlDecoder,
                                       @NotNull FullHttpRequest request,
                                       @NotNull ChannelHandlerContext context) {

        Project project = getLastFocusedOrOpenedProject();
        if (project == null || project.isDisposed()) {
            sendJsonRpcError(context, request,
                    "2.0", null,
                    -32001,
                    "No open project to run MCPSearchService on",
                    null);
            return null;
        }

        try {
            JSONObject rpc = readJsonBody(request);
            JSONObject response = handleRpc(project, rpc);

            // --- SSE: если клиент просит text/event-stream, оборачиваем в SSE-ивент ---
            String accept = request.headers().get(HttpHeaderNames.ACCEPT);
            boolean wantsSse = accept != null
                    && accept.toLowerCase().contains("text/event-stream");

            if (wantsSse) {
                sendSseResponse(context, request, response);
            } else {
                sendJsonResponse(context, request, response);
            }
        } catch (Exception e) {
            LOG.warn("Error handling MCP HTTP request", e);
            sendJsonRpcError(context, request,
                    "2.0", null,
                    -32000,
                    "Internal error: " + e.getMessage(),
                    null);
        }

        return null;
    }

    // =========================
    // MCP JSON-RPC обработка
    // =========================

    private JSONObject handleRpc(@NotNull Project project, @NotNull JSONObject rpc) {
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
            LOG.warn("Exception in MCP handler", e);
            JSONObject data = new JSONObject()
                    .put("exception", e.getClass().getName())
                    .put("message", String.valueOf(e.getMessage()));
            return rpcError(jsonrpc, id, -32000, "Internal MCP server error", data);
        }
    }

    // --- initialize ---

    private JSONObject handleInitialize(String jsonrpc, Object id, @Nullable JSONObject params) {
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

    private static final String TOOL_RETRIEVE_DOCS = "lsfusion_retrieve_docs";
    private static final String TOOL_RETRIEVE_HOWTOS = "lsfusion_retrieve_howtos";
    private static final String TOOL_RETRIEVE_COMMUNITY = "lsfusion_retrieve_community";
    private static final String TOOL_VALIDATE_SYNTAX = "lsfusion_validate_syntax";
    private static final String TOOL_GET_GUIDANCE = "lsfusion_get_guidance";

    // --- tools/list ---

    private JSONObject handleToolsList(String jsonrpc, Object id, @Nullable JSONObject params) {
        JSONArray tools = new JSONArray();
        tools.put(buildFindElementsToolDescriptor());
        tools.put(buildQueryToolDescriptor(TOOL_RETRIEVE_DOCS, "Fetch prioritized chunks from lsFusion RAG store (official documentation and language reference) — based on a single search query."));
        tools.put(buildQueryToolDescriptor(TOOL_RETRIEVE_HOWTOS, "Fetch prioritized chunks from lsFusion RAG store (examples for combined tasks / scenarios and how-tos) — based on a single search query."));
        tools.put(buildQueryToolDescriptor(TOOL_RETRIEVE_COMMUNITY, "Fetch prioritized chunks from lsFusion RAG store (tutorials, articles, and community discussions) — based on a single search query. Use this ONLY for deep, ambiguous tasks when other retrieval tools (docs, howtos) did not provide a solution."));
        tools.put(buildValidateSyntaxToolDescriptor());
        tools.put(buildGetGuidanceToolDescriptor());

        JSONObject result = new JSONObject().put("tools", tools);
        return rpcResult(jsonrpc, id, result);
    }

    private JSONObject buildQueryToolDescriptor(String name, String description) {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("query", new JSONObject()
                                .put("type", "string")
                                .put("description", "Query")))
                .put("required", new JSONArray().put("query"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", name)
                .put("description", description)
                .put("inputSchema", inputSchema);
    }

    private JSONObject buildValidateSyntaxToolDescriptor() {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "string")
                                .put("description", "Text")))
                .put("required", new JSONArray().put("text"))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_VALIDATE_SYNTAX)
                .put("description", "Validate the syntax of the list of lsFusion statements. Use this ONLY when IDE tools for error checking and code execution are not available.")
                .put("inputSchema", inputSchema);
    }

    private JSONObject buildGetGuidanceToolDescriptor() {
        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject())
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_GET_GUIDANCE)
                .put("description", "Fetch the brief overview and mandatory rules for working with lsFusion. IMPORTANT: The assistant MUST call this tool before ANY task related to lsFusion if it's not already in your context. The assistant MUST read and strictly follow all rules and guidelines provided by this tool for ANY lsFusion-related task.")
                .put("inputSchema", inputSchema);
    }

    private JSONObject buildFindElementsToolDescriptor() {
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
                        .put("name", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Element name filter as CSV (comma-separated). Word if valid ID, else Java regex."))
                        .put("query", new JSONObject()
                                .put("type", "string")
                                .put("description",
                                        "Semantic query for local RAG (vector search). CSV allowed."))
                        .put("useVectorSearch", new JSONObject()
                                .put("type", "boolean")
                                .put("default", false)
                                .put("description",
                                        "If true, use local vector search for `query`. If false, use standard filters (names/contains)."))
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
                                        "Additional filter objects of the same structure as the root. JSON array string (e.g. `[{\"name\":\"Foo\", \"contains\":\"bar\", \"modules\" : \"MyModule\"},{\"query\":\"Foo\", \"useVectorSearch\":true}]`). Results are merged (OR)."))
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

    private JSONObject handleRemoteToolCall(String jsonrpc, Object id, String toolName, JSONObject arguments) {
        try {
            String payload = RemoteMcpClient.callRemoteTool(toolName, arguments);

            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(
                            new JSONObject()
                                    .put("type", "text")
                                    .put("text", payload)
                    ))
                    .put("isError", false);

            return rpcResult(jsonrpc, id, callResult);
        } catch (Exception e) {
            LOG.warn(toolName + " failed", e);
            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(
                            new JSONObject()
                                    .put("type", "text")
                                    .put("text", toolName + " error: " + e.getMessage())
                    ))
                    .put("isError", true);

            return rpcResult(jsonrpc, id, callResult);
        }
    }

    // --- tools/call ---

    private JSONObject handleToolsCall(Project project,
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

        if (TOOL_RETRIEVE_DOCS.equals(name) ||
                TOOL_RETRIEVE_HOWTOS.equals(name) ||
                TOOL_RETRIEVE_COMMUNITY.equals(name) ||
                TOOL_VALIDATE_SYNTAX.equals(name) ||
                TOOL_GET_GUIDANCE.equals(name)) {
            return handleRemoteToolCall(jsonrpc, id, name, arguments);
        }

        if (!TOOL_NAME.equals(name)) {
            return rpcError(jsonrpc, id, -32601, "Unknown tool: " + name, null);
        }

        try {
            JSONObject payloadObj = MCPSearchUtils.findElements(project, arguments);

            // Some MCP clients only accept content types: text, image, audio, resource_link, resource.
            // So we serialize JSON into a text block to avoid schema validation errors.
            String payload = payloadObj.toString();

            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(
                            new JSONObject()
                                    .put("type", "text")
                                    .put("text", payload)
                    ))
                    .put("isError", false);

            return rpcResult(jsonrpc, id, callResult);
        } catch (Exception e) {
            LOG.warn(TOOL_NAME + " failed", e);
            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(
                            new JSONObject()
                                    .put("type", "text")
                                    .put("text", TOOL_NAME + " error: " + e.getMessage())
                    ))
                    .put("isError", true);

            return rpcResult(jsonrpc, id, callResult);
        }
    }

    // =========================
    // JSON-RPC helpers
    // =========================

    private static JSONObject rpcResult(String jsonrpc, Object id, JSONObject result) {
        JSONObject resp = new JSONObject().put("jsonrpc", jsonrpc);
        if (id != null) {
            resp.put("id", id);
        }
        resp.put("result", result);
        return resp;
    }

    private static JSONObject rpcError(String jsonrpc,
                                       Object id,
                                       int code,
                                       String message,
                                       @Nullable JSONObject data) {
        JSONObject error = new JSONObject()
                .put("code", code)
                .put("message", message);
        if (data != null) {
            error.put("data", data);
        }
        JSONObject resp = new JSONObject().put("jsonrpc", jsonrpc);
        if (id != null) {
            resp.put("id", id);
        }
        resp.put("error", error);
        return resp;
    }

    // =========================
    // HTTP helpers
    // =========================

    private static JSONObject readJsonBody(@NotNull FullHttpRequest request) throws Exception {
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

    private void sendJsonResponse(@NotNull ChannelHandlerContext ctx,
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

    // --- SSE-ответ: один event с JSON-RPC-resp, потом закрываем соединение ---

    private void sendSseResponse(@NotNull ChannelHandlerContext ctx,
                                 @NotNull FullHttpRequest req,
                                 @NotNull JSONObject json) {

        // Один SSE-событие "message" с полным JSON-RPC-ответом
        String payload = json.toString();
        String event =
                "event: message\n" +
                        "data: " + payload + "\n\n";

        byte[] bytes = event.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                buf
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=utf-8");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.PRAGMA, "no-cache");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        // Можно оставить keep-alive, но для простоты закрываем после ответа
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        sendResponse(req, ctx, response);
    }

    private void sendJsonRpcError(@NotNull ChannelHandlerContext ctx,
                                  @NotNull FullHttpRequest req,
                                  @NotNull String jsonrpc,
                                  @Nullable Object id,
                                  int code,
                                  @NotNull String message,
                                  @Nullable JSONObject data) {
        JSONObject error = rpcError(jsonrpc, id, code, message, data);
        sendJsonResponse(ctx, req, error);
    }
}
