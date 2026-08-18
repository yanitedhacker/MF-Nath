package com.mrbitches.doomsy.data

import android.content.Context
import com.mrbitches.doomsy.llm.Exchange

class DoomsySessionStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isIntroSeen(): Boolean = prefs.getBoolean(KEY_INTRO_SEEN, false)

    fun setIntroSeen() {
        prefs.edit().putBoolean(KEY_INTRO_SEEN, true).apply()
    }

    fun isTtsMuted(): Boolean = prefs.getBoolean(KEY_TTS_MUTED, false)

    fun setTtsMuted(muted: Boolean) {
        prefs.edit().putBoolean(KEY_TTS_MUTED, muted).apply()
    }

    fun loadMessages(): List<Message> =
        DoomsySessionCodec.decodeMessages(prefs.getString(KEY_MESSAGES, "").orEmpty())

    fun saveMessages(messages: List<Message>) {
        prefs.edit()
            .putString(KEY_MESSAGES, DoomsySessionCodec.encodeMessages(messages))
            .apply()
    }

    fun loadHistory(): List<Exchange> =
        DoomsySessionCodec.decodeHistory(prefs.getString(KEY_HISTORY, "").orEmpty())

    fun saveHistory(history: List<Exchange>) {
        prefs.edit()
            .putString(KEY_HISTORY, DoomsySessionCodec.encodeHistory(history))
            .apply()
    }

    fun clearConversation() {
        prefs.edit()
            .remove(KEY_MESSAGES)
            .remove(KEY_HISTORY)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "doomsy_session"
        private const val KEY_INTRO_SEEN = "intro_seen"
        private const val KEY_MESSAGES = "messages"
        private const val KEY_HISTORY = "history"
        private const val KEY_TTS_MUTED = "tts_muted"
    }
}
