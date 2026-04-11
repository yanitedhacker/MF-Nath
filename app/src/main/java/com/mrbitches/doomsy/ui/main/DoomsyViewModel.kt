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
import android.util.Log
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

    private val _isModelLoading = MutableStateFlow(false)
    val isModelLoading: StateFlow<Boolean> = _isModelLoading

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
        if (!llamaBridge.isLoaded() && !_isModelLoading.value) {
            loadModel()
        }
    }

    fun closeChat() {
        _isChatOpen.value = false
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        if (!llamaBridge.isLoaded()) {
            val userMessage = Message(text = text, isUser = true)
            _messages.value = _messages.value + userMessage
            val errorMsg = Message(
                text = "The villain's mind is still loading... patience.",
                isUser = false,
            )
            _messages.value = _messages.value + errorMsg
            return
        }

        val userMessage = Message(text = text, isUser = true)
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = conversationManager.buildPrompt(text)
            Log.d(TAG, "Prompt length: ${prompt.length} chars")

            val response = try {
                withTimeout(120_000L) {
                    llamaBridge.generate(prompt, maxTokens = 256)
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                Log.w(TAG, "Generation timed out after 120s")
                null
            } catch (e: Exception) {
                Log.e(TAG, "Generation error", e)
                null
            }

            val cleanResponse = if (response.isNullOrBlank() || response.startsWith("[Error")) {
                Log.w(TAG, "Bad response: $response")
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
        _isModelLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val modelFile = File(context.filesDir, MODEL_FILENAME)

            if (!modelFile.exists()) {
                Log.d(TAG, "Reassembling model from asset chunks...")
                try {
                    FileOutputStream(modelFile).use { output ->
                        for (chunk in MODEL_CHUNKS) {
                            context.assets.open(chunk).use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                    Log.d(TAG, "Model reassembled: ${modelFile.length()} bytes")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reassemble model", e)
                    modelFile.delete()
                    _isModelLoading.value = false
                    return@launch
                }
            }

            Log.d(TAG, "Loading model into llama.cpp...")
            val loaded = llamaBridge.loadModel(modelFile.absolutePath, contextSize = 2048)
            _isModelLoaded.value = loaded
            _isModelLoading.value = false
            Log.d(TAG, "Model loaded: $loaded")
        }
    }

    companion object {
        private const val TAG = "DoomsyViewModel"
        private const val MODEL_FILENAME = "gemma-4-e2b-q4_k_m.gguf"
        private val MODEL_CHUNKS = listOf(
            "gemma-4-e2b-q4_k_m.gguf.part_aa",
            "gemma-4-e2b-q4_k_m.gguf.part_ab",
        )
    }

    override fun onCleared() {
        super.onCleared()
        llamaBridge.unloadModel()
    }
}
