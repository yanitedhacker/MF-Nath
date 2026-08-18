package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.VillainOrange
import com.mrbitches.doomsy.util.DeviceGraphics
import com.mrbitches.doomsy.util.GraphicsProfile
import com.mrbitches.doomsy.util.Haptic
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

@Composable
fun DoomsyHero(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    scaleToUnits: Float = 0.72f,
    verticalOffset: Float = 0.02f,
    horizontalOffset: Float = -0.12f,
    idleRotationSpan: Float = 4f,
) {
    val context = LocalContext.current
    val profile = remember(context) { DeviceGraphics.profile(context) }
    val loadGlb = remember(context) { DeviceGraphics.shouldLoadGlb(context) }

    if (!loadGlb) {
        DoomsyMaskFallback(
            modifier = modifier,
            onTap = onTap,
            onLongPress = onLongPress,
        )
        return
    }

    val downscale = profile == GraphicsProfile.Medium
    DoomsyViewer(
        modifier = modifier,
        onTap = onTap,
        onLongPress = onLongPress,
        scaleToUnits = if (downscale) scaleToUnits * 0.68f else scaleToUnits,
        verticalOffset = verticalOffset,
        horizontalOffset = horizontalOffset,
        idleRotationSpan = if (downscale) idleRotationSpan * 0.5f else idleRotationSpan,
        autoAnimate = !downscale,
    )
}

@Composable
fun DoomsyViewer(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    scaleToUnits: Float = 0.72f,
    verticalOffset: Float = 0.02f,
    horizontalOffset: Float = -0.12f,
    idleRotationSpan: Float = 4f,
    autoAnimate: Boolean = true,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environment = rememberEnvironment(engine)

    val infiniteTransition = rememberInfiniteTransition(label = "idle")

    val breathOffset by infiniteTransition.animateFloat(
        initialValue = -0.012f,
        targetValue = 0.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathY",
    )

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

    Box(
        modifier = modifier.semantics {
            contentDescription = "Doomsy mask. Double tap for a quip."
            onClick {
                onTap()
                true
            }
        },
    ) {
        SceneView(
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
            rememberModelInstance(modelLoader, DeviceGraphics.GLB_ASSET_PATH)?.let { modelInstance ->
                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = targetScale,
                    autoAnimate = autoAnimate,
                    position = Float3(xOffset, yOffset + breathOffset, 0f),
                    rotation = Float3(0f, idleRotation, 0f),
                )
            }
        }
    }
}

@Composable
fun DoomsyMaskFallback(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .semantics {
                contentDescription = "Doomsy mask fallback. Double tap for a quip."
                onClick {
                    onTap()
                    true
                }
            }
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
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)
            drawCircle(
                color = Color(0xFFE8E8EA),
                radius = w * 0.42f,
                center = center,
            )
            drawCircle(
                color = DeepBlack,
                radius = w * 0.42f,
                center = center,
                style = Stroke(width = w * 0.035f),
            )
            drawCircle(
                color = VillainOrange,
                radius = w * 0.42f,
                center = center,
                style = Stroke(width = w * 0.012f),
            )

            val eyeWidth = w * 0.18f
            val eyeHeight = h * 0.08f
            val eyeY = h * 0.42f
            drawRoundRect(
                color = DeepBlack,
                topLeft = Offset(w * 0.28f - eyeWidth / 2f, eyeY),
                size = Size(eyeWidth, eyeHeight),
                cornerRadius = CornerRadius(eyeHeight / 2f, eyeHeight / 2f),
            )
            drawRoundRect(
                color = DeepBlack,
                topLeft = Offset(w * 0.72f - eyeWidth / 2f, eyeY),
                size = Size(eyeWidth, eyeHeight),
                cornerRadius = CornerRadius(eyeHeight / 2f, eyeHeight / 2f),
            )

            drawRoundRect(
                color = VillainOrange.copy(alpha = 0.85f),
                topLeft = Offset(w * 0.46f, h * 0.62f),
                size = Size(w * 0.08f, h * 0.16f),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
    }
}
