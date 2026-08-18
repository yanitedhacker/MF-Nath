package com.mrbitches.doomsy.cloud

import com.mrbitches.doomsy.BuildConfig
import com.mrbitches.doomsy.llm.Exchange
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

data class DoomsyCloudReply(
    val text: String,
    val model: String?,
    val source: String?,
)

class DoomsyCloudClient(
    private val baseUrl: String = BuildConfig.DOOMSY_API_BASE_URL,
    private val apiKey: String = BuildConfig.DOOMSY_API_KEY,
) {

    fun isConfigured(): Boolean = baseUrl.isNotBlank()

    fun probeHealth(): Boolean {
        if (!isConfigured()) return false

        val connection = (URL(healthEndpoint(baseUrl)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Doomsy/${BuildConfig.VERSION_NAME}")
            applyApiKey()
        }

        return try {
            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun sendMessage(
        userMessage: String,
        history: List<Exchange>,
    ): DoomsyCloudReply {
        return streamMessage(userMessage, history, onToken = {})
    }

    @Throws(IOException::class)
    fun streamMessage(
        userMessage: String,
        history: List<Exchange>,
        onToken: (String) -> Unit,
    ): DoomsyCloudReply {
        if (!isConfigured()) {
            throw IOException("Cloud endpoint is not configured")
        }

        val connection = (URL(chatEndpoint(baseUrl)).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 45_000
            doOutput = true
            setUseCaches(false)
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "text/event-stream, application/json")
            setRequestProperty("User-Agent", "Doomsy/${BuildConfig.VERSION_NAME}")
            applyApiKey()
        }

        val payload = JSONObject()
            .put("message", userMessage)
            .put("history", JSONArray().apply {
                history.takeLast(6).forEach { exchange ->
                    put(
                        JSONObject()
                            .put("user", exchange.userMessage)
                            .put("assistant", exchange.assistantResponse),
                    )
                }
            })

        try {
            connection.outputStream.use { output ->
                output.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            val status = connection.responseCode
            val contentType = connection.contentType.orEmpty().lowercase()
            if (status !in 200..299) {
                val body = readBody(connection, successful = false)
                throw IOException("Cloud chat failed ($status): $body")
            }

            return if (contentType.contains("text/event-stream")) {
                readSseReply(connection, onToken)
            } else {
                readJsonReply(connection, onToken)
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun HttpURLConnection.applyApiKey() {
        if (apiKey.isNotBlank()) {
            setRequestProperty(API_KEY_HEADER, apiKey)
        }
    }

    private fun readSseReply(
        connection: HttpURLConnection,
        onToken: (String) -> Unit,
    ): DoomsyCloudReply {
        val assembled = StringBuilder()
        connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            while (true) {
                val line = reader.readLine() ?: break
                val token = DoomsySseParser.tokenFromSseLine(line) ?: continue
                assembled.append(token)
                onToken(token)
            }
        }

        val reply = assembled.toString().trim()
        if (reply.isBlank()) {
            throw IOException("Cloud chat stream returned an empty reply")
        }
        return DoomsyCloudReply(
            text = reply,
            model = null,
            source = "cloudflare-workers-ai",
        )
    }

    private fun readJsonReply(
        connection: HttpURLConnection,
        onToken: (String) -> Unit,
    ): DoomsyCloudReply {
        val body = readBody(connection, successful = true)
        val json = JSONObject(body)
        val reply = json.optString("reply").trim()
        if (reply.isBlank()) {
            throw IOException("Cloud chat returned an empty reply")
        }
        onToken(reply)
        return DoomsyCloudReply(
            text = reply,
            model = json.optString("model").ifBlank { null },
            source = json.optString("source").ifBlank { null },
        )
    }

    private fun readBody(connection: HttpURLConnection, successful: Boolean): String {
        val stream = if (successful) connection.inputStream else connection.errorStream
        return stream
            ?.bufferedReader(StandardCharsets.UTF_8)
            ?.use { it.readText() }
            .orEmpty()
    }

    companion object {
        const val API_KEY_HEADER = "X-Doomsy-Key"

        fun chatEndpoint(baseUrl: String): String {
            val trimmed = baseUrl.trim().trimEnd('/')
            return if (trimmed.endsWith("/chat")) trimmed else "$trimmed/chat"
        }

        fun healthEndpoint(baseUrl: String): String {
            val trimmed = baseUrl.trim().trimEnd('/')
            val origin = if (trimmed.endsWith("/chat")) trimmed.removeSuffix("/chat") else trimmed
            return "${origin.trimEnd('/')}/health"
        }
    }
}
