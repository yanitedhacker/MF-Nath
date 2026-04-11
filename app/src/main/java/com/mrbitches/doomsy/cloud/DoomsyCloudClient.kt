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

    @Throws(IOException::class)
    fun sendMessage(
        userMessage: String,
        history: List<Exchange>,
    ): DoomsyCloudReply {
        if (!isConfigured()) {
            throw IOException("Cloud endpoint is not configured")
        }

        val endpoint = if (baseUrl.endsWith("/chat")) baseUrl else "${baseUrl.trimEnd('/')}/chat"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
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
}
