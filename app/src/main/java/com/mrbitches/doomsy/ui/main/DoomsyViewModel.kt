package com.mrbitches.doomsy.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrbitches.doomsy.cloud.DoomsyCloudClient
import com.mrbitches.doomsy.data.DoomsyQuips
import com.mrbitches.doomsy.data.DoomsySessionStore
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.llm.ConversationManager
import com.mrbitches.doomsy.llm.DoomsyFallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoomsyViewModel(app: Application) : AndroidViewModel(app) {

    private val cloudClient = DoomsyCloudClient()
    private val conversationManager = ConversationManager(maxExchanges = 6)
    private val sessionStore = DoomsySessionStore(app)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isCloudConfigured = MutableStateFlow(cloudClient.isConfigured())
    val isCloudConfigured: StateFlow<Boolean> = _isCloudConfigured.asStateFlow()

    private val _isCloudReachable = MutableStateFlow(false)
    val isCloudReachable: StateFlow<Boolean> = _isCloudReachable.asStateFlow()

    private val _currentQuip = MutableStateFlow<String?>(null)
    val currentQuip: StateFlow<String?> = _currentQuip.asStateFlow()

    init {
        restoreSession()
        seedOpeningMessageIfNeeded()
        probeCloud()
    }

    fun triggerQuip() {
        _currentQuip.value = DoomsyQuips.random()
    }

    fun triggerDeepQuip() {
        _currentQuip.value = DoomsyQuips.randomDeep()
    }

    fun dismissQuip() {
        _currentQuip.value = null
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        val trimmedText = text.trim()
        appendMessage(trimmedText, isUser = true)
        _isGenerating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val cleanResponse = try {
                if (!_isCloudConfigured.value) {
                    Log.w(TAG, "Cloud endpoint missing; using fallback reply")
                    DoomsyFallbacks.replyTo(trimmedText)
                } else {
                    val cloudReply = cloudClient.sendMessage(trimmedText, conversationManager.history)
                    _isCloudReachable.value = true
                    Log.d(TAG, "Cloud reply from ${cloudReply.source ?: "unknown source"} (${cloudReply.model ?: "unknown model"})")
                    cloudReply.text
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cloud chat failed", e)
                _isCloudReachable.value = false
                DoomsyFallbacks.replyTo(trimmedText)
            }

            conversationManager.addExchange(trimmedText, cleanResponse)
            appendMessage(cleanResponse, isUser = false, animateReveal = true)
            persistSession()
            _isGenerating.value = false
        }
    }

    private fun restoreSession() {
        val savedMessages = sessionStore.loadMessages()
        if (savedMessages.isNotEmpty()) {
            _messages.value = savedMessages
        }
        sessionStore.loadHistory().forEach { exchange ->
            conversationManager.addExchange(exchange.userMessage, exchange.assistantResponse)
        }
    }

    private fun persistSession() {
        sessionStore.saveMessages(_messages.value)
        sessionStore.saveHistory(conversationManager.history)
    }

    private fun probeCloud() {
        if (!_isCloudConfigured.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _isCloudReachable.value = cloudClient.probeHealth()
        }
    }

    private fun seedOpeningMessageIfNeeded() {
        if (_messages.value.isNotEmpty()) return
        appendMessage(DoomsyFallbacks.openingLine, isUser = false, animateReveal = true)
        persistSession()
    }

    private fun appendMessage(text: String, isUser: Boolean, animateReveal: Boolean = false) {
        _messages.value = _messages.value + Message(
            text = text,
            isUser = isUser,
            animateReveal = animateReveal,
        )
    }

    companion object {
        private const val TAG = "DoomsyViewModel"
    }
}
