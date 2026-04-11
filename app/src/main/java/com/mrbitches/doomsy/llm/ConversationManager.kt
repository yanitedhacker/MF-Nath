package com.mrbitches.doomsy.llm

data class Exchange(
    val userMessage: String,
    val assistantResponse: String,
)

class ConversationManager(private val maxExchanges: Int = 10) {

    private val _history = mutableListOf<Exchange>()
    val history: List<Exchange> get() = _history

    fun addExchange(userMessage: String, assistantResponse: String) {
        _history.add(Exchange(userMessage, assistantResponse))
        while (_history.size > maxExchanges) {
            _history.removeAt(0)
        }
    }

    fun buildPrompt(userMessage: String): String {
        val sb = StringBuilder()

        sb.appendLine("<start_of_turn>user")
        sb.appendLine("System: ${DoomsyPersonality.systemPrompt}")
        sb.appendLine("<end_of_turn>")
        sb.appendLine("<start_of_turn>model")
        sb.appendLine("Understood. Doomsy is ready. The villain awaits.")
        sb.appendLine("<end_of_turn>")

        for (exchange in _history) {
            sb.appendLine("<start_of_turn>user")
            sb.appendLine(exchange.userMessage)
            sb.appendLine("<end_of_turn>")
            sb.appendLine("<start_of_turn>model")
            sb.appendLine(exchange.assistantResponse)
            sb.appendLine("<end_of_turn>")
        }

        sb.appendLine("<start_of_turn>user")
        sb.appendLine(userMessage)
        sb.appendLine("<end_of_turn>")
        sb.appendLine("<start_of_turn>model")

        return sb.toString()
    }

    fun clear() {
        _history.clear()
    }
}
