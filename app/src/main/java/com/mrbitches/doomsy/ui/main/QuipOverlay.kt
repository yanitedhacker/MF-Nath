package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.ui.theme.GlassBlack
import com.mrbitches.doomsy.ui.theme.GoldSubtle
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Anim
import kotlinx.coroutines.delay

@Composable
fun QuipOverlay(
    quip: String?,
    modifier: Modifier = Modifier,
    onDismissed: () -> Unit = {},
) {
    var visible by remember(quip) { mutableStateOf(quip != null) }

    LaunchedEffect(quip) {
        if (quip != null) {
            visible = true
            delay(Anim.QUIP_DISPLAY_DURATION)
            visible = false
            delay(Anim.QUIP_FADE_DURATION)
            onDismissed()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(spring()) + scaleIn(
                initialScale = 0.85f,
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 300f,
                ),
            ),
            exit = fadeOut() + scaleOut(targetScale = 0.9f),
        ) {
            val shape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(GlassBlack)
                    .border(0.5.dp, GoldSubtle, shape)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = quip ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = OffWhite,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}
