package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.mrbitches.doomsy.util.Haptic
import io.github.sceneview.Scene
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberModelInstance

@Composable
fun DoomsyViewer(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    scaleToUnits: Float = 0.72f,
    verticalOffset: Float = 0.02f,
    horizontalOffset: Float = -0.12f,
    idleRotationSpan: Float = 4f,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environment = rememberEnvironment(engine)

    val infiniteTransition = rememberInfiniteTransition(label = "idle")

    // Subtle breathing bob
    val breathOffset by infiniteTransition.animateFloat(
        initialValue = -0.012f,
        targetValue = 0.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathY",
    )

    // Slow idle sway
    val idleRotation by infiniteTransition.animateFloat(
        initialValue = -idleRotationSpan,
        targetValue = idleRotationSpan,
        animationSpec = infiniteRepeatable(
            animation = tween(7000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idleRotation",
    )

    val targetScale by animateFloatAsState(
        targetValue = scaleToUnits,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 150f),
        label = "scale",
    )

    val yOffset by animateFloatAsState(
        targetValue = verticalOffset,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 150f),
        label = "yOffset",
    )
    val xOffset by animateFloatAsState(
        targetValue = horizontalOffset,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 150f),
        label = "xOffset",
    )

    Box(modifier = modifier) {
        Scene(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Haptic.tap(context)
                            onTap()
                        },
                        onLongPress = {
                            Haptic.press(context)
                            onLongPress()
                        },
                    )
                },
            engine = engine,
            modelLoader = modelLoader,
            environment = environment,
            isOpaque = false,
        ) {
            rememberModelInstance(modelLoader, "models/doomsy.glb")?.let { modelInstance ->
                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = targetScale,
                    autoAnimate = true,
                    position = Float3(xOffset, yOffset + breathOffset, 0f),
                    rotation = Float3(0f, idleRotation, 0f),
                )
            }
        }
    }
}
