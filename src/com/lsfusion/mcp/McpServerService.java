package com.lsfusion.mcp;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * MCP single endpoint that supports BOTH:
 *
 * 1) Legacy SSE transport (MCP Inspector-friendly):
 *    - GET  /lsfusion                       -> opens SSE stream and sends `event: endpoint` with POST URL containing ?sessionId=...
 *    - POST /lsfusion?sessionId=...         -> executes JSON-RPC and pushes `event: message` into that SSE stream, returns 202
 *
 * 2) Streamable HTTP (simple JSON-RPC over POST):
 *    - POST /lsfusion                       -> returns JSON response
 *
 * Keep-alive: sends ": ping" comments periodically to prevent intermediate proxies from closing the stream.
 */
public final class McpServerService extends McpBaseService {

    private static final Logger LOG = Logger.getInstance(McpServerService.class);

    // sessionId -> SSE channel ctx
    private static final Map<String, ChannelHandlerContext> SSE_SESSIONS = new ConcurrentHashMap<>();
    // sessionId -> keepalive future
    private static final Map<String, ScheduledFuture<?>> SSE_KEEPALIVES = new ConcurrentHashMap<>();

    // tune as needed
    private static final long KEEPALIVE_SECONDS = 20;

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    /**
     * Junie/IDEA (kotlin sdk) can require "id" to always exist in JSONRPCResponse.
     * We keep this ON, but we still avoid emitting SSE message events for true JSON-RPC notifications (no id).
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
            if (HttpMethod.GET.equals(request.method())) {
                // Legacy SSE opener (Inspector expects this handshake)
                openSseStreamAndSendEndpoint(context);
                return null;
            }

            if (!HttpMethod.POST.equals(request.method())) {
                sendPlainStatus(context, request, HttpResponseStatus.METHOD_NOT_ALLOWED, "Only GET/POST supported");
                return null;
            }

            String sessionId = urlDecoder.parameters()
                    .getOrDefault("sessionId", List.of())
                    .stream().findFirst().orElse(null);

            if (sessionId != null && !sessionId.isBlank()) {
                handlePostToSseSession(sessionId, request, context);
            } else {
                handleSinglePost(request, context);
            }
        } catch (Exception e) {
            LOG.warn("Error handling MCP request", e);
            sendPlainStatus(context, request, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    "Internal error: " + e.getMessage());
        }

        return null;
    }

    // =========================
    // POST handlers
    // =========================

    private void handlePostToSseSession(@NotNull String sessionId,
                                        @NotNull FullHttpRequest request,
                                        @NotNull ChannelHandlerContext context) throws Exception {

        ChannelHandlerContext sseCtx = SSE_SESSIONS.get(sessionId);
        if (sseCtx == null || !sseCtx.channel().isActive()) {
            sendPlainStatus(context, request, HttpResponseStatus.GONE, "SSE session not found or closed");
            return;
        }

        JSONObject rpc = readJsonBody(request);

        // Do not emit `event: message` for JSON-RPC notifications (id is missing)
        Object id = rpc.opt("id");
        boolean isNotification = (id == null || id == JSONObject.NULL);

        JSONObject response = handleRpcOrProjectError(rpc);

        if (!isNotification) {
            sendSseEvent(sseCtx, "message", response.toString());
        }

        sendAccepted(context, request);
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

    // =========================
    // SSE open + keepalive
    // =========================

    private void openSseStreamAndSendEndpoint(@NotNull ChannelHandlerContext ctx) {
        String sessionId = UUID.randomUUID().toString();

        SSE_SESSIONS.put(sessionId, ctx);
        ctx.channel().closeFuture().addListener(f -> cleanupSession(sessionId));

        // Open SSE stream (IMPORTANT: no Content-Length; keep alive)
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=utf-8");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.PRAGMA, "no-cache");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        // Optional but harmless; helps with some proxy setups
        response.headers().set("X-Accel-Buffering", "no");

        ctx.writeAndFlush(response);

        // MCP Inspector expects an endpoint with sessionId in query string for POST back-channel
        String endpoint = "/" + getServiceName() + "?sessionId=" + sessionId;
        sendSseEvent(ctx, "endpoint", endpoint);

        // Keep-alive pings to avoid idle timeouts (comments are valid SSE)
        ScheduledFuture<?> fut = ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()) {
                sendSseComment(ctx, "ping");
            }
        }, KEEPALIVE_SECONDS, KEEPALIVE_SECONDS, TimeUnit.SECONDS);

        SSE_KEEPALIVES.put(sessionId, fut);

        // Immediate ping improves perceived responsiveness in some clients
        sendSseComment(ctx, "hello");
    }

    private void cleanupSession(@NotNull String sessionId) {
        SSE_SESSIONS.remove(sessionId);
        ScheduledFuture<?> fut = SSE_KEEPALIVES.remove(sessionId);
        if (fut != null) {
            fut.cancel(false);
        }
    }

    private void sendSseEvent(@NotNull ChannelHandlerContext ctx,
                              @NotNull String eventName,
                              @NotNull String data) {
        StringBuilder sb = new StringBuilder();
        sb.append("event: ").append(eventName).append("\n");
        for (String line : data.split("\\r?\\n")) {
            sb.append("data: ").append(line).append("\n");
        }
        sb.append("\n");
        ByteBuf buf = Unpooled.copiedBuffer(sb.toString(), StandardCharsets.UTF_8);
        ctx.writeAndFlush(new DefaultHttpContent(buf));
    }

    private void sendSseComment(@NotNull ChannelHandlerContext ctx,
                                @NotNull String comment) {
        ByteBuf buf = Unpooled.copiedBuffer(":" + comment + "\n\n", StandardCharsets.UTF_8);
        ctx.writeAndFlush(new DefaultHttpContent(buf));
    }

    // =========================
    // HTTP helpers
    // =========================

    private void sendAccepted(@NotNull ChannelHandlerContext ctx,
                              @NotNull FullHttpRequest req) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.ACCEPTED);
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
        sendResponse(req, ctx, resp);
    }

    private void sendNoContent(@NotNull ChannelHandlerContext ctx,
                               @NotNull FullHttpRequest req) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
        sendResponse(req, ctx, resp);
    }

    private void sendPlainStatus(@NotNull ChannelHandlerContext ctx,
                                 @NotNull FullHttpRequest req,
                                 @NotNull HttpResponseStatus status,
                                 @NotNull String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        sendResponse(req, ctx, resp);
    }
}
