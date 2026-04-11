package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberModelInstance

@Composable
fun DoomsyViewer(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    scaledDown: Boolean = false,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environment = rememberEnvironment(engine)

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathY",
    )

    val headSway by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "headSway",
    )

    val targetScale = if (scaledDown) 0.7f else 1.0f

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
                ).apply {
                    position = Position(y = breathOffset)
                    rotation = Rotation(y = headSway)
                }
            }
        }
    }
}
