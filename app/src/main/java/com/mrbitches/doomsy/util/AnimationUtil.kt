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

    const val INTRO_SLIDE_HOLD = 2200L
    const val INTRO_SLIDE_TRANSITION = 750L
    const val INTRO_OUTRO_FADE = 600L

    const val QUIP_DISPLAY_DURATION = 3000L
    const val QUIP_FADE_DURATION = 400L
}
