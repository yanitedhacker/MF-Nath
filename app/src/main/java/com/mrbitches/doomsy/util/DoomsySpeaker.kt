package com.mrbitches.doomsy.util

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class DoomsySpeaker(app: Application) {

    @Volatile
    private var ready = false
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(app.applicationContext) { status ->
            val engine = tts
            if (status == TextToSpeech.SUCCESS && engine != null) {
                engine.language = Locale.US
                engine.setPitch(0.86f)
                engine.setSpeechRate(0.92f)
                ready = true
            } else {
                Log.w(TAG, "Text-to-speech is unavailable")
            }
        }
    }

    fun speak(text: String) {
        val spoken = text.trim()
        val engine = tts
        if (!ready || engine == null || spoken.isBlank()) return
        engine.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        ready = false
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val TAG = "DoomsySpeaker"
        private const val UTTERANCE_ID = "doomsy-reply"
    }
}
