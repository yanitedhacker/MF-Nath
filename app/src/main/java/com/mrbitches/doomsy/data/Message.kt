package com.mrbitches.doomsy.data

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val animateReveal: Boolean = false,
)
