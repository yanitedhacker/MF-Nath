package com.mrbitches.doomsy.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrbitches.doomsy.data.DoomsyQuips
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.llm.ConversationManager
import com.mrbitches.doomsy.llm.LlamaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.FileOutputStream

class DoomsyViewModel(app: Application) : AndroidViewModel(app) {

    private val llamaBridge = LlamaBridge()
    private val conversationManager = ConversationManager()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded

    private val _currentQuip = MutableStateFlow<String?>(null)
    val currentQuip: StateFlow<String?> = _currentQuip

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen

    fun triggerQuip() {
        _currentQuip.value = DoomsyQuips.random()
    }

    fun dismissQuip() {
        _currentQuip.value = null
    }

    fun openChat() {
        _isChatOpen.value = true
        if (!llamaBridge.isLoaded()) {
            loadModel()
        }
    }

    fun closeChat() {
        _isChatOpen.value = false
        viewModelScope.launch(Dispatchers.IO) {
            llamaBridge.unloadModel()
            _isModelLoaded.value = false
        }
        conversationManager.clear()
        _messages.value = emptyList()
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        val userMessage = Message(text = text, isUser = true)
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            if (!llamaBridge.isLoaded()) {
                val errorMsg = Message(
                    text = "The villain's mind is elsewhere. Try again.",
                    isUser = false,
                )
                _messages.value = _messages.value + errorMsg
                _isGenerating.value = false
                return@launch
            }

            val prompt = conversationManager.buildPrompt(text)

            val response = try {
                withTimeout(10_000L) {
                    llamaBridge.generate(prompt, maxTokens = 256)
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                null
            }

            val cleanResponse = if (response.isNullOrBlank() || response.startsWith("[Error")) {
                "Doomsy contemplates in silence..."
            } else {
                response.trim()
            }

            conversationManager.addExchange(text, cleanResponse)

            val doomsyMessage = Message(text = cleanResponse, isUser = false)
            _messages.value = _messages.value + doomsyMessage
            _isGenerating.value = false
        }
    }

    private fun loadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val modelFile = File(context.filesDir, "gemma-4-e2b-q4_k_m.gguf")

            if (!modelFile.exists()) {
                context.assets.open("gemma-4-e2b-q4_k_m.gguf").use { input ->
                    FileOutputStream(modelFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val loaded = llamaBridge.loadModel(modelFile.absolutePath, contextSize = 1024)
            _isModelLoaded.value = loaded
        }
    }

    override fun onCleared() {
        super.onCleared()
        llamaBridge.unloadModel()
    }
}
