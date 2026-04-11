package com.mrbitches.doomsy.ui.intro

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.FogSilver
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.GoldSubtle
import com.mrbitches.doomsy.ui.theme.Ivory
import com.mrbitches.doomsy.ui.theme.Pearl
import com.mrbitches.doomsy.ui.theme.SilverSmoke
import com.mrbitches.doomsy.util.Anim
import kotlinx.coroutines.delay

private val introSlides = listOf(
    "Carti fan allegations still active.",
    "Car money still uncollected. Doomsy remembers.",
    "Nathaniel Leo Messi Syiem stays on the villain's ledger.",
    "Mr. Bitches sent the mask so the room don't go empty.",
)

@Composable
fun IntroScreen(onIntroComplete: () -> Unit) {
    var slideIndex by remember { mutableIntStateOf(0) }
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (slideIndex < introSlides.lastIndex) {
            delay(Anim.INTRO_SLIDE_HOLD)
            slideIndex++
        }
        delay(Anim.INTRO_SLIDE_HOLD)
        screenAlpha.animateTo(0f, tween(Anim.INTRO_OUTRO_FADE.toInt()))
        onIntroComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Pearl, Ivory, FogSilver),
                ),
            )
            .alpha(screenAlpha.value),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Pearl.copy(alpha = 0.95f),
                            Pearl.copy(alpha = 0f),
                        ),
                        radius = 900f,
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .width(2.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GoldSubtle.copy(alpha = 0f),
                            GoldDim.copy(alpha = 0.5f),
                            GoldSubtle.copy(alpha = 0f),
                        ),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Crossfade(
                targetState = slideIndex,
                animationSpec = tween(Anim.INTRO_SLIDE_TRANSITION.toInt()),
                label = "introSlides",
            ) { currentIndex ->
                Text(
                    text = introSlides[currentIndex],
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = DeepBlack,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp,
                    ),
                )
            }
        }

        Text(
            text = "DOOMSY",
            style = MaterialTheme.typography.labelMedium.copy(
                color = SilverSmoke,
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 52.dp),
        )
    }
}
