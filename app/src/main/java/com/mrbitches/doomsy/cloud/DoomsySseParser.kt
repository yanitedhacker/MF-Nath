package com.mrbitches.doomsy.cloud

import org.json.JSONObject

object DoomsySseParser {

    fun tokenFromSseLine(line: String): String? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("data:")) return null
        return tokenFromData(trimmed.substring(5))
    }

    fun tokenFromData(raw: String): String? {
        val data = raw.trim()
        if (data.isEmpty() || data == "[DONE]") return null

        return try {
            val json = JSONObject(data)
            if (json.optBoolean("done", false)) return null

            json.optString("response").takeIf { it.isNotEmpty() }
                ?: json.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("delta")
                    ?.optString("content")
                    ?.takeIf { it.isNotEmpty() }
        } catch (_: Exception) {
            null
        }
    }
}
