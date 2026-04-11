package com.mrbitches.doomsy.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrbitches.doomsy.cloud.DoomsyCloudClient
import com.mrbitches.doomsy.data.DoomsyQuips
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.llm.ConversationManager
import com.mrbitches.doomsy.llm.DoomsyFallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class DoomsyViewModel(app: Application) : AndroidViewModel(app) {

    private val cloudClient = DoomsyCloudClient()
    private val conversationManager = ConversationManager(maxExchanges = 6)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _isCloudConfigured = MutableStateFlow(cloudClient.isConfigured())
    val isCloudConfigured: StateFlow<Boolean> = _isCloudConfigured

    private val _isCloudReachable = MutableStateFlow(false)
    val isCloudReachable: StateFlow<Boolean> = _isCloudReachable

    private val _currentQuip = MutableStateFlow<String?>(null)
    val currentQuip: StateFlow<String?> = _currentQuip

    init {
        seedOpeningMessageIfNeeded()
    }

    fun triggerQuip() {
        _currentQuip.value = DoomsyQuips.random()
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
            appendMessage(cleanResponse, isUser = false)
            _isGenerating.value = false
        }
    }

    private fun seedOpeningMessageIfNeeded() {
        if (_messages.value.isNotEmpty()) return
        appendMessage(DoomsyFallbacks.openingLine, isUser = false)
    }

    private fun appendMessage(text: String, isUser: Boolean) {
        _messages.value = _messages.value + Message(text = text, isUser = isUser)
    }

    companion object {
        private const val TAG = "DoomsyViewModel"
    }
}
