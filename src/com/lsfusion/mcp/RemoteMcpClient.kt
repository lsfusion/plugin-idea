package com.lsfusion.mcp

import com.intellij.mcpserver.McpExpectedError
import com.intellij.util.io.HttpRequests
import org.json.JSONObject
import java.net.HttpURLConnection

object RemoteMcpClient {
    private const val URL = "https://ai.lsfusion.org/mcp"
    private const val DEFAULT_TIMEOUT_SECONDS = 30

    @Volatile
    private var sessionId: String? = null

    private fun extractJsonFromSseOrPlain(body: String): String {
        val idx = body.indexOf("data:")
        if (idx >= 0) {
            val after = body.substring(idx + "data:".length)
            return after.lineSequence().firstOrNull()?.trim().orEmpty()
        }
        return body.trim()
    }

    @JvmStatic
    fun mcpExpectedError(message: String): McpExpectedError {
        return try {
            // Reflective access to McpExpectedError to handle different versions/classloaders if needed
            // although here it seems we are in the same module.
            // But let's follow the pattern from McpToolset.kt
            val constructor = try {
                McpExpectedError::class.java.getConstructor(String::class.java, Class.forName("kotlinx.serialization.json.JsonObject"))
            } catch (e: Exception) {
                McpExpectedError::class.java.getConstructor(String::class.java)
            }
            if (constructor.parameterCount == 2) {
                constructor.newInstance(message, null)
            } else {
                constructor.newInstance(message)
            }
        } catch (e: Exception) {
            // Fallback if McpExpectedError is totally unavailable (should not happen in this plugin)
            throw RuntimeException(message, e)
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
                val connection = req.connection as HttpURLConnection
                val code = connection.responseCode
                if (code in 200..299) {
                    req.readString()
                } else {
                    val errorText = try {
                        connection.errorStream?.bufferedReader()?.readText() ?: req.readString()
                    } catch (e: Exception) {
                        try {
                            req.readString()
                        } catch (_: Exception) {
                            "Code $code (no body)"
                        }
                    }

                    if (code == 400 || code == 401) {
                        synchronized(this) {
                            if (sessionId == session) {
                                sessionId = null
                            }
                        }
                    }
                    throw mcpExpectedError("Remote MCP HTTP error $code: ${errorText.take(2_000)}")
                }
            }
    }

    private fun ensureSessionId(timeoutSeconds: Int): String {
        val existing = sessionId
        if (!existing.isNullOrBlank()) return existing

        synchronized(this) {
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

            var headerSessionId: String? = null
            val responseBody = HttpRequests.post(URL, "application/json")
                .connectTimeout(timeoutSeconds * 1000)
                .readTimeout(timeoutSeconds * 1000)
                .accept("application/json, text/event-stream")
                .connect { req ->
                    val connection = req.connection as HttpURLConnection
                    req.write(requestBody)
                    val code = connection.responseCode
                    if (code !in 200..299) {
                        val err = connection.errorStream?.bufferedReader()?.readText() ?: ""
                        throw mcpExpectedError("Remote MCP initialize failed with $code: $err")
                    }
                    val resp = req.readString()
                    headerSessionId = connection.getHeaderField("mcp-session-id")
                    resp
                }

            if (headerSessionId.isNullOrBlank()) {
                throw mcpExpectedError("Remote MCP initialize did not return 'mcp-session-id'. Body: ${responseBody.take(2_000)}")
            }

            sessionId = headerSessionId
            return headerSessionId!!
        }
    }

    /**
     * Calls a remote tool and returns the text from the first content item.
     * Useful for tools that return a single text block (which might be a JSON string).
     */
    @JvmStatic
    @JvmOverloads
    fun callRemoteTool(
        toolName: String,
        arguments: JSONObject,
        timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS
    ): String {
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

        var sid = ensureSessionId(timeoutSeconds)
        var responseText: String
        try {
            responseText = doPostJson(requestBody, timeoutSeconds, sid)
        } catch (e: Exception) {
            if (sessionId == null) { // Session was reset in doPostJson due to 400/401
                sid = ensureSessionId(timeoutSeconds)
                responseText = doPostJson(requestBody, timeoutSeconds, sid)
            } else {
                throw e
            }
        }

        val jsonText = extractJsonFromSseOrPlain(responseText)

        try {
            val root = JSONObject(jsonText)
            val result = root.optJSONObject("result")
                ?: throw IllegalStateException("Missing 'result' in remote response")

            val content = result.optJSONArray("content")
                ?: throw IllegalStateException("Missing 'content' in remote result")

            if (content.length() == 0) {
                throw IllegalStateException("Empty 'content' in remote result")
            }

            val firstContent = content.getJSONObject(0)
            val text = firstContent.optString("text", null)
                ?: throw IllegalStateException("Missing 'text' in remote content")

            if (text.startsWith("Error executing tool")) {
                throw mcpExpectedError(text)
            }

            return text
        } catch (e: McpExpectedError) {
            throw e
        } catch (e: Exception) {
            throw mcpExpectedError("Remote MCP tool '$toolName' call failed: ${e.message}")
        }
    }
}
