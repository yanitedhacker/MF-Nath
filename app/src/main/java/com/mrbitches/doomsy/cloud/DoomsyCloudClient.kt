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
        if (!isConfigured()) {
            throw IOException("Cloud endpoint is not configured")
        }

        val connection = (URL(chatEndpoint(baseUrl)).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 45_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "Doomsy/${BuildConfig.VERSION_NAME}")
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
            val body = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader(StandardCharsets.UTF_8)
                ?.use { it.readText() }
                .orEmpty()

            if (status !in 200..299) {
                throw IOException("Cloud chat failed ($status): $body")
            }

            val json = JSONObject(body)
            val reply = json.optString("reply").trim()
            if (reply.isBlank()) {
                throw IOException("Cloud chat returned an empty reply")
            }

            return DoomsyCloudReply(
                text = reply,
                model = json.optString("model").ifBlank { null },
                source = json.optString("source").ifBlank { null },
            )
        } finally {
            connection.disconnect()
        }
    }

    companion object {
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
