package com.mrbitches.doomsy.cloud

object DoomsySseParser {

    private val jsonStringField = Regex(""""(\w+)"\s*:\s*"((?:\\.|[^"\\])*)"""")
    private val doneTrue = Regex(""""done"\s*:\s*true""")

    fun tokenFromSseLine(line: String): String? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("data:")) return null
        return tokenFromData(trimmed.substring(5))
    }

    fun tokenFromData(raw: String): String? {
        val data = raw.trim()
        if (data.isEmpty() || data == "[DONE]") return null
        if (doneTrue.containsMatchIn(data)) return null

        val fields = jsonStringField.findAll(data).associate { match ->
            match.groupValues[1] to unescapeJson(match.groupValues[2])
        }
        return fields["response"]?.takeIf { it.isNotEmpty() }
            ?: fields["content"]?.takeIf { it.isNotEmpty() }
    }

    private fun unescapeJson(value: String): String {
        val builder = StringBuilder(value.length)
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char == '\\' && index + 1 < value.length) {
                when (value[index + 1]) {
                    'n' -> builder.append('\n')
                    't' -> builder.append('\t')
                    '"' -> builder.append('"')
                    '\\' -> builder.append('\\')
                    '/' -> builder.append('/')
                    else -> builder.append(value[index + 1])
                }
                index += 2
            } else {
                builder.append(char)
                index++
            }
        }
        return builder.toString()
    }
}
