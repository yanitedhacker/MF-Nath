package com.mrbitches.doomsy.data

object ChatExport {

    fun format(messages: List<Message>): String {
        if (messages.isEmpty()) {
            return "Doomsy chat\n===========\n\n(empty)"
        }

        val body = messages.joinToString("\n\n") { message ->
            val speaker = if (message.isUser) "You" else "Doomsy"
            "[$speaker]\n${message.text.trim()}"
        }
        return "Doomsy chat\n===========\n\n$body"
    }
}
