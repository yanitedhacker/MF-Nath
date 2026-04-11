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

    fun clear() {
        _history.clear()
    }
}
