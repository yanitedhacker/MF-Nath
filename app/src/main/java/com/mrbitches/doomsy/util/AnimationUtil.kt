package com.mrbitches.doomsy.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object Anim {
    fun <T> bouncy() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    fun <T> gentle() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
    )

    fun <T> snappy() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    const val INTRO_RUMBLE_DELAY = 500L
    const val INTRO_MASK_FADE_START = 1000L
    const val INTRO_MASK_FADE_DURATION = 1500L
    const val INTRO_MESSAGE_FADE_START = 3500L
    const val INTRO_MESSAGE_FADE_DURATION = 800L
    const val INTRO_TOTAL_DURATION = 5500L
    const val INTRO_FADE_OUT_DURATION = 600L

    const val QUIP_DISPLAY_DURATION = 3000L
    const val QUIP_FADE_DURATION = 400L

    const val TYPEWRITER_CHAR_DELAY = 30L
}
