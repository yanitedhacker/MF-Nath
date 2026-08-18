package com.mrbitches.doomsy.data

import com.mrbitches.doomsy.llm.Exchange

object DoomsySessionCodec {

    const val MAX_STORED_MESSAGES = 40
    const val MAX_STORED_EXCHANGES = 6

    fun encodeMessages(messages: List<Message>): String {
        return messages.takeLast(MAX_STORED_MESSAGES).joinToString("\n") { message ->
            val role = if (message.isUser) "u" else "a"
            "$role\t${message.timestamp}\t${escape(message.text)}"
        }
    }

    fun decodeMessages(raw: String): List<Message> {
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            val parts = line.split("\t", limit = 3)
            if (parts.size != 3) return@mapNotNull null
            val text = unescape(parts[2]).trim()
            if (text.isBlank()) return@mapNotNull null
            Message(
                text = text,
                isUser = parts[0] == "u",
                timestamp = parts[1].toLongOrNull() ?: 0L,
                animateReveal = false,
            )
        }.toList()
    }

    fun encodeHistory(history: List<Exchange>): String {
        return history.takeLast(MAX_STORED_EXCHANGES).joinToString("\n") { exchange ->
            "${escape(exchange.userMessage)}\t${escape(exchange.assistantResponse)}"
        }
    }

    fun decodeHistory(raw: String): List<Exchange> {
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            val parts = line.split("\t", limit = 2)
            if (parts.size != 2) return@mapNotNull null
            val user = unescape(parts[0]).trim()
            val assistant = unescape(parts[1]).trim()
            if (user.isBlank() || assistant.isBlank()) return@mapNotNull null
            Exchange(userMessage = user, assistantResponse = assistant)
        }.toList()
    }

    internal fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
    }

    internal fun unescape(value: String): String {
        val builder = StringBuilder(value.length)
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char == '\\' && index + 1 < value.length) {
                when (value[index + 1]) {
                    'n' -> {
                        builder.append('\n')
                        index += 2
                        continue
                    }
                    't' -> {
                        builder.append('\t')
                        index += 2
                        continue
                    }
                    '\\' -> {
                        builder.append('\\')
                        index += 2
                        continue
                    }
                }
            }
            builder.append(char)
            index++
        }
        return builder.toString()
    }
}
