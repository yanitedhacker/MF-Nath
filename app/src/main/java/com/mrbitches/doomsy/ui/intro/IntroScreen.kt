package com.mrbitches.doomsy.ui.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.ui.theme.Void
import com.mrbitches.doomsy.util.Anim
import com.mrbitches.doomsy.util.Haptic
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(onIntroComplete: () -> Unit) {
    val context = LocalContext.current

    val glintAlpha = remember { Animatable(0f) }
    val maskAlpha = remember { Animatable(0f) }
    val messageAlpha = remember { Animatable(0f) }
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        delay(Anim.INTRO_RUMBLE_DELAY)
        Haptic.introRumble(context)

        delay(Anim.INTRO_MASK_FADE_START - Anim.INTRO_RUMBLE_DELAY)
        glintAlpha.animateTo(1f, tween(400))
        maskAlpha.animateTo(1f, tween(Anim.INTRO_MASK_FADE_DURATION.toInt()))

        delay(Anim.INTRO_MESSAGE_FADE_START - Anim.INTRO_MASK_FADE_START - Anim.INTRO_MASK_FADE_DURATION)
        messageAlpha.animateTo(1f, tween(Anim.INTRO_MESSAGE_FADE_DURATION.toInt()))

        delay(Anim.INTRO_TOTAL_DURATION - Anim.INTRO_MESSAGE_FADE_START - Anim.INTRO_MESSAGE_FADE_DURATION)
        screenAlpha.animateTo(0f, tween(Anim.INTRO_FADE_OUT_DURATION.toInt()))

        onIntroComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .alpha(screenAlpha.value),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp),
        ) {
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(glintAlpha.value),
                ) {
                    drawMaskSilhouette(maskAlpha.value)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Since your ass is busy (again),\nMr. Bitches sent me, pussy",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.alpha(messageAlpha.value),
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMaskSilhouette(alpha: Float) {
    val maskColor = Gold.copy(alpha = alpha)
    val darkColor = Void.copy(alpha = alpha)

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(maskColor, GoldDim.copy(alpha = alpha)),
        ),
        size = size,
    )

    val slitWidth = size.width * 0.15f
    val slitHeight = size.height * 0.08f
    val slitY = size.height * 0.38f

    drawRect(
        color = darkColor,
        topLeft = Offset(size.width * 0.22f, slitY),
        size = Size(slitWidth, slitHeight),
    )
    drawRect(
        color = darkColor,
        topLeft = Offset(size.width * 0.63f, slitY),
        size = Size(slitWidth, slitHeight),
    )
}
