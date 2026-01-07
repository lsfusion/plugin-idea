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

public final class McpServerService extends RestService {

    public McpServerService() {
    }

    private static final Logger LOG = Logger.getInstance(McpServerService.class);

    private static final String MCP_PROTOCOL_VERSION = "2024-11-05";
    private static final String TOOL_NAME = "lsf_find_elements";

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

    // --- tools/list ---

    private JSONObject handleToolsList(String jsonrpc, Object id, @Nullable JSONObject params) {
        JSONObject tool = buildFindElementsToolDescriptor();
        JSONObject result = new JSONObject()
                .put("tools", new JSONArray().put(tool));
        return rpcResult(jsonrpc, id, result);
    }

    private JSONObject buildFindElementsToolDescriptor() {
        // Base schema for a single filter object (without `moreFilters` to avoid recursive schema).
        JSONObject filterSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject()
                        .put("modules", new JSONObject()
                                .put("type", "array")
                                .put("description",
                                        "Module scope filter")
                                .put("items", new JSONObject()
                                        .put("type", "object")
                                        .put("properties", new JSONObject()
                                                .put("name", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description", "lsFusion module name as in `MODULE <name>` / `REQUIRE <name>`, e.g. \"MyModule\"."))
                                                .put("only", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description",
                                                                "Scope behavior: omitted = REQUIRE expansion and may include libraries (wider). " +
                                                                        "`project` = REQUIRE expansion but project content only. " +
                                                                        "`this` = only the file that declares the module (no REQUIRE expansion).")
                                                        .put("enum", new JSONArray().put("project").put("this"))))
                                        .put("required", new JSONArray().put("name"))
                                        .put("additionalProperties", false)))
                        .put("names", new JSONObject()
                                .put("type", "array")
                                .put("description",
                                        "Name/text matchers (OR across items). Each matcher is AND across provided fields. " +
                                                "The match target is controlled by `inCode` (see `inCode` below). " +
                                                "Performance: `word` (length >= 3) can act as a fast prefilter;")
                                .put("items", new JSONObject()
                                        .put("type", "object")
                                        .put("properties", new JSONObject()
                                                .put("word", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description",
                                                                "Logical match: case-insensitive substring. " +
                                                                        "This prefilter is case-sensitive and token-based, so it may miss matches if casing differs or the text is split differently. " +
                                                                        "If you need fully case-insensitive / non-token substring matching, prefer `regex` without `word` (slower but more complete).")
                                                        .put("examples", new JSONArray().put("cust").put("order")))
                                                .put("regex", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description",
                                                                "Java Pattern, `.find()`. Use `(?i)` for ignore-case. If regex fails to compile, it is ignored.")
                                                        .put("examples", new JSONArray().put("(?i)cust.*").put("\\\\bORDER\\\\b")))
                                                .put("inCode", new JSONObject()
                                                        .put("type", "boolean")
                                                        .put("default", false)
                                                        .put("description", "`false` = applied to element names; `true` = applied to element code text.")))
                                        .put("oneOf", new JSONArray()
                                                .put(new JSONObject().put("required", new JSONArray().put("word")))
                                                .put(new JSONObject().put("required", new JSONArray().put("regex"))))
                                        .put("additionalProperties", false)))
                        .put("elementTypes", new JSONObject()
                                .put("type", "array")
                                .put("description", "Element type filter. If set, only these element types are returned.")
                                .put("items", new JSONObject()
                                        .put("type", "string")
                                        .put("enum", new JSONArray()
                                                .put("module")
                                                .put("metacode")
                                                .put("class")
                                                .put("property")
                                                .put("action")
                                                .put("form")
                                                .put("navigatorElement")
                                                .put("window")
                                                .put("group")
                                                .put("table")
                                                .put("event")
                                                .put("calculatedEvent")
                                                .put("constraint")
                                                .put("index"))))
                        .put("classes", new JSONObject()
                                .put("type", "array")
                                .put("description",
                                        "Class filter: [{name}]. Name may be canonical (with namespace) or short (no namespace). " +
                                                "For property/action elements: matches parameter classes (best-effort).")
                                .put("items", new JSONObject()
                                        .put("type", "object")
                                        .put("properties", new JSONObject()
                                                .put("name", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description", "Class name, e.g. \"MyNS.MyClass\" or \"MyClass\".")))
                                        .put("required", new JSONArray().put("name"))
                                        .put("additionalProperties", false)))
                        .put("relatedElements", new JSONObject()
                                .put("type", "array")
                                .put("description",
                                        "Usage-graph traversal seeds. Each item must identify a seed element by `name` (named) OR `location` (unnamed). " +
                                                "`name` may be canonical or short (namespace optional). For property/action elements you may include a signature in brackets, " +
                                                "e.g. \"MyNS.myProp[MyNS.MyClass]\" or \"myProp[MyClass]\". Traversal is best-effort and may be large.")
                                .put("items", new JSONObject()
                                        .put("type", "object")
                                        .put("properties", new JSONObject()
                                                .put("type", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description", "Expected element type (best-effort). Required if name is set, otherwise name is ignored."))
                                                .put("name", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description",
                                                                "Seed name for named elements. Canonical form is accepted; namespace is optional. " +
                                                                        "For property/action elements, a parameter signature in brackets is supported."))
                                                .put("location", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description",
                                                                "Seed location for unnamed elements: `<module>(<line>:<col>)`, 1-based. Example: `MyModule(10:5)`."))
                                                .put("only", new JSONObject()
                                                        .put("type", "string")
                                                        .put("description", "Traversal direction: `uses` (forward), `used` (reverse), if omitted - both directions")
                                                        .put("enum", new JSONArray().put("uses").put("used").put("both")))))
                                .put("oneOf", new JSONArray()
                                        .put(new JSONObject().put("required", new JSONArray().put("name")))
                                        .put(new JSONObject().put("required", new JSONArray().put("location"))))
                                .put("additionalProperties", false)))
            .put("additionalProperties", false);

        // Allow nesting `moreFilters` only inside the primary filter.
        JSONObject filterWithMoreSchema = new JSONObject()
                .put("type", "object")
                .put("properties", new JSONObject(filterSchema.getJSONObject("properties").toMap())
                        .put("moreFilters", new JSONObject()
                                .put("type", "array")
                                .put("description",
                                        "Additional filter objects (same structure as `filter`). Each filter is executed independently; results are merged (OR).")
                                .put("items", filterSchema)));

        JSONObject inputSchema = new JSONObject()
                .put("type", "object")
                .put("title", "lsFusion code search query")
                .put("description",
                        "Search lsFusion elements using `filter` (AND within one filter). " +
                                "Use `filter.moreFilters` for OR (merge). If `filter` is omitted, search runs without filters.")
                .put("properties", new JSONObject()
                        .put("filter", new JSONObject()
                                .put("description", "Primary filter (AND within). Use `moreFilters` for OR.")
                                .put("type", "object")
                                .put("properties", filterWithMoreSchema.getJSONObject("properties")))
                        .put("minSymbols", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 0)
                                .put("default", MCPSearchUtils.DEFAULT_MIN_SYMBOLS)
                                .put("description",
                                        "Best-effort minimum output size (JSON chars). If too small, server may append neighboring elements from the same files until reaching this value (maxSymbols is a hard cap)."))
                        .put("maxSymbols", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 1)
                                .put("default", MCPSearchUtils.DEFAULT_MAX_SYMBOLS)
                                .put("description", "Hard cap for total output size (JSON chars)."))
                        .put("timeoutSeconds", new JSONObject()
                                .put("type", "integer")
                                .put("minimum", 1)
                                .put("default", MCPSearchUtils.DEFAULT_TIMEOUT_SECS)
                                .put("description", "Best-effort wall-clock timeout for the request (integer seconds).")))
                .put("additionalProperties", false);

        return new JSONObject()
                .put("name", TOOL_NAME)
                .put("description",
                        "Find and inspect lsFusion elements in the current IntelliJ project. " +
                                "Returns JSON as text: {items:[...], meta?:string}. " +
                                "meta values: `too long - timeout hit` | `too large - max symbols hit` | `too small - non matching elements added`.")
                .put("inputSchema", inputSchema);
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
        if (!TOOL_NAME.equals(name)) {
            return rpcError(jsonrpc, id, -32601, "Unknown tool: " + name, null);
        }

        JSONObject arguments = params.optJSONObject("arguments");
        if (arguments == null) {
            arguments = new JSONObject();
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
            LOG.warn("lsf_find_elements failed", e);
            JSONObject callResult = new JSONObject()
                    .put("content", new JSONArray().put(
                            new JSONObject()
                                    .put("type", "text")
                                    .put("text", "lsf_find_elements error: " + e.getMessage())
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
