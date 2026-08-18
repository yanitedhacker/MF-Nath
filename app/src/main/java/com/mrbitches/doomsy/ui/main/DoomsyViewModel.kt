package com.mrbitches.doomsy.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrbitches.doomsy.cloud.DoomsyCloudClient
import com.mrbitches.doomsy.data.ChatExport
import com.mrbitches.doomsy.data.DoomsyQuips
import com.mrbitches.doomsy.data.DoomsySessionStore
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.llm.ConversationManager
import com.mrbitches.doomsy.llm.DoomsyFallbacks
import com.mrbitches.doomsy.util.DoomsySpeaker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoomsyViewModel(app: Application) : AndroidViewModel(app) {

    private val cloudClient = DoomsyCloudClient()
    private val conversationManager = ConversationManager(maxExchanges = 6)
    private val sessionStore = DoomsySessionStore(app)
    private val speaker = DoomsySpeaker(app)

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

    private val _isTtsMuted = MutableStateFlow(sessionStore.isTtsMuted())
    val isTtsMuted: StateFlow<Boolean> = _isTtsMuted.asStateFlow()

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

    fun setTtsMuted(muted: Boolean) {
        _isTtsMuted.value = muted
        sessionStore.setTtsMuted(muted)
        if (muted) {
            speaker.stop()
        }
    }

    fun exportChatText(): String = ChatExport.format(_messages.value)

    fun clearChat() {
        speaker.stop()
        conversationManager.clear()
        _messages.value = emptyList()
        sessionStore.clearConversation()
        seedOpeningMessageIfNeeded()
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        val trimmedText = text.trim()
        speaker.stop()
        appendMessage(trimmedText, isUser = true)
        _isGenerating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val assembled = StringBuilder()
            val applyToken: (String) -> Unit = { token ->
                assembled.append(token)
                updateAssistantDraft(assembled.toString())
            }

            try {
                if (!_isCloudConfigured.value) {
                    Log.w(TAG, "Cloud endpoint missing; using fallback reply")
                    applyToken(DoomsyFallbacks.replyTo(trimmedText))
                } else {
                    val cloudReply = cloudClient.streamMessage(
                        trimmedText,
                        conversationManager.history,
                        applyToken,
                    )
                    _isCloudReachable.value = true
                    Log.d(
                        TAG,
                        "Cloud reply from ${cloudReply.source ?: "unknown source"} (${cloudReply.model ?: "unknown model"})",
                    )
                    if (assembled.isBlank()) {
                        applyToken(cloudReply.text)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cloud chat failed", e)
                _isCloudReachable.value = false
                if (assembled.isBlank()) {
                    assembled.clear()
                    applyToken(DoomsyFallbacks.replyTo(trimmedText))
                }
            }

            val cleanResponse = assembled.toString().trim()
            conversationManager.addExchange(trimmedText, cleanResponse)
            persistSession()
            _isGenerating.value = false
            if (!_isTtsMuted.value && cleanResponse.isNotBlank()) {
                speaker.speak(cleanResponse)
            }
        }
    }

    override fun onCleared() {
        speaker.shutdown()
        super.onCleared()
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
        appendMessage(DoomsyFallbacks.openingLine, isUser = false)
        persistSession()
    }

    private fun appendMessage(text: String, isUser: Boolean) {
        _messages.value = _messages.value + Message(
            text = text,
            isUser = isUser,
        )
    }

    private fun updateAssistantDraft(text: String) {
        val current = _messages.value
        val last = current.lastOrNull()
        _messages.value = if (last != null && !last.isUser) {
            current.dropLast(1) + last.copy(text = text)
        } else {
            current + Message(text = text, isUser = false)
        }
    }

    companion object {
        private const val TAG = "DoomsyViewModel"
    }
}
