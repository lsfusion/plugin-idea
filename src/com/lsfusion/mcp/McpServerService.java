package com.lsfusion.mcp;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.io.Responses;
import org.json.JSONObject;

/**
 * MCP single endpoint over the IDE built-in server, using the Streamable HTTP transport:
 *
 *   POST /lsfusion   -> executes JSON-RPC and returns the JSON response (notifications get 204).
 *
 * The same lsFusion tools are also exposed through the bundled JetBrains MCP Server plugin
 * (see {@link McpToolset}); this endpoint is an additional direct HTTP entry point.
 *
 * The legacy HTTP+SSE transport (MCP 2024-11-05) was removed: it required writing chunked
 * responses via the platform-internal io.netty.buffer API, and Streamable HTTP supersedes it.
 */
public final class McpServerService extends McpBaseService {

    private static final Logger LOG = Logger.getInstance(McpServerService.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /**
     * Junie/IDEA (kotlin sdk) can require "id" to always exist in JSONRPCResponse.
     */
    @Override
    protected boolean useFallbackId() {
        return true;
    }

    @NotNull
    @Override
    public String getServiceName() {
        // single endpoint path
        return "lsfusion";
    }

    @Override
    protected boolean isPrefixlessAllowed() {
        return true;
    }

    @Override
    protected boolean isMethodSupported(@NotNull HttpMethod method) {
        return HttpMethod.GET.equals(method) || HttpMethod.POST.equals(method);
    }

    @Override
    public boolean isAccessible(@NotNull HttpRequest request) {
        return true;
    }

    @Override
    public @Nullable String execute(@NotNull QueryStringDecoder urlDecoder,
                                    @NotNull FullHttpRequest request,
                                    @NotNull ChannelHandlerContext context) {
        try {
            if (!HttpMethod.POST.equals(request.method())) {
                sendPlainStatus(context, request, HttpResponseStatus.METHOD_NOT_ALLOWED,
                        "Only POST (MCP Streamable HTTP) is supported");
                return null;
            }
            handleSinglePost(request, context);
        } catch (Exception e) {
            LOG.warn("Error handling MCP request", e);
            sendPlainStatus(context, request, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    "Internal error: " + e.getMessage());
        }

        return null;
    }

    private void handleSinglePost(@NotNull FullHttpRequest request,
                                  @NotNull ChannelHandlerContext context) throws Exception {

        JSONObject rpc = readJsonBody(request);

        Object id = rpc.opt("id");
        boolean isNotification = (id == null || id == JSONObject.NULL);

        // For notifications, correct behavior is: no JSON-RPC response body.
        if (isNotification) {
            sendNoContent(context, request);
            return;
        }

        JSONObject response = handleRpcOrProjectError(rpc);
        sendJsonResponse(context, request, response);
    }

    private JSONObject handleRpcOrProjectError(@NotNull JSONObject rpc) {
        Project project = getLastFocusedOrOpenedProject();
        if (project == null || project.isDisposed()) {
            return rpcError("2.0", rpc.opt("id"), -32001,
                    "No open project to run MCPSearchService on", null);
        }
        return handleRpc(project, rpc);
    }

    private void sendNoContent(@NotNull ChannelHandlerContext ctx,
                               @NotNull FullHttpRequest req) {
        Responses.send(HttpResponseStatus.NO_CONTENT, ctx.channel(), req);
    }

    private void sendPlainStatus(@NotNull ChannelHandlerContext ctx,
                                 @NotNull FullHttpRequest req,
                                 @NotNull HttpResponseStatus status,
                                 @NotNull String text) {
        Responses.sendPlainText(status, ctx.channel(), req, text);
    }
}
