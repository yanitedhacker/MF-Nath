package com.mrbitches.doomsy.ui.intro

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrbitches.doomsy.ui.theme.AshGrey
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.MadvillainSilver
import com.mrbitches.doomsy.ui.theme.StarkGrey
import com.mrbitches.doomsy.ui.theme.StarkWhite
import com.mrbitches.doomsy.ui.theme.VillainOrangeDim
import com.mrbitches.doomsy.ui.theme.VillainOrangeSubtle
import com.mrbitches.doomsy.util.Anim
import com.mrbitches.doomsy.util.Haptic
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
    val context = LocalContext.current
    val accessibilityManager = remember(context) {
        context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE)
            as? android.view.accessibility.AccessibilityManager
    }
    val reduceMotion = accessibilityManager?.isTouchExplorationEnabled == true

    LaunchedEffect(Unit) {
        Haptic.introRumble(context)
        if (reduceMotion) {
            delay(400)
            onIntroComplete()
            return@LaunchedEffect
        }
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
                    colors = listOf(StarkWhite, StarkGrey, AshGrey),
                ),
            )
            .alpha(screenAlpha.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onIntroComplete,
            )
            .semantics {
                contentDescription = "Doomsy intro. Double tap to skip."
            },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            StarkWhite.copy(alpha = 0.95f),
                            StarkWhite.copy(alpha = 0f),
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
                            VillainOrangeSubtle.copy(alpha = 0f),
                            VillainOrangeDim.copy(alpha = 0.5f),
                            VillainOrangeSubtle.copy(alpha = 0f),
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
                color = MadvillainSilver,
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 52.dp),
        )

        Text(
            text = "Tap to enter",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MadvillainSilver,
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp),
        )
    }
}
