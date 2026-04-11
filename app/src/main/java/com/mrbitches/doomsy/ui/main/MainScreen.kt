package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrbitches.doomsy.ui.chat.ChatComposer
import com.mrbitches.doomsy.ui.chat.ChatMessageStack
import com.mrbitches.doomsy.ui.chat.ChatSignalChip
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.DimGrey
import com.mrbitches.doomsy.ui.theme.FogSilver
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassIvory
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.GoldSubtle
import com.mrbitches.doomsy.ui.theme.Ivory
import com.mrbitches.doomsy.ui.theme.Pearl
import com.mrbitches.doomsy.ui.theme.SoftGoldBorder
import com.mrbitches.doomsy.ui.theme.StormInk

@Composable
fun MainScreen(viewModel: DoomsyViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isCloudConfigured by viewModel.isCloudConfigured.collectAsState()
    val isCloudReachable by viewModel.isCloudReachable.collectAsState()
    val currentQuip by viewModel.currentQuip.collectAsState()
    var isMusicExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Pearl),
    ) {
        AtmosphericBackdrop()

        DoomsyViewer(
            modifier = Modifier.fillMaxSize(),
            onTap = { viewModel.triggerQuip() },
            onLongPress = {},
            scaleToUnits = 0.72f,
            verticalOffset = 0.03f,
            horizontalOffset = -0.14f,
            idleRotationSpan = 3.6f,
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.48f)
                .align(Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Ivory.copy(alpha = 0.32f),
                            Pearl.copy(alpha = 0.8f),
                        ),
                    ),
                ),
        )

        QuipOverlay(
            quip = currentQuip,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 92.dp, top = 72.dp),
            onDismissed = { viewModel.dismissQuip() },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                StatusCluster(
                    isGenerating = isGenerating,
                    isCloudConfigured = isCloudConfigured,
                    isCloudReachable = isCloudReachable,
                )

                UtilityPill(
                    text = if (isMusicExpanded) "Hide ^" else "Tracks v",
                    onClick = { isMusicExpanded = !isMusicExpanded },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.66f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val signalText = when {
                        !isCloudConfigured -> "Off-grid mode. Doomsy speaks from the vault."
                        !isCloudReachable && messages.size > 1 -> "Signal unstable. Doomsy still answers through the static."
                        else -> null
                    }

                    signalText?.let { text ->
                        ChatSignalChip(text = text)
                    }

                    ChatMessageStack(
                        messages = messages,
                        isGenerating = isGenerating,
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            MusicTray(
                expanded = isMusicExpanded,
                onToggle = { isMusicExpanded = !isMusicExpanded },
            )

            Spacer(modifier = Modifier.size(12.dp))

            ChatComposer(
                onSend = { viewModel.sendMessage(it) },
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AtmosphericBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Pearl, Ivory, FogSilver),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Pearl.copy(alpha = 0.95f),
                            Color.Transparent,
                        ),
                        radius = 1250f,
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Ivory.copy(alpha = 0.12f),
                            Pearl.copy(alpha = 0.56f),
                        ),
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
                            GoldDim.copy(alpha = 0.52f),
                            GoldSubtle.copy(alpha = 0f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun StatusCluster(
    isGenerating: Boolean,
    isCloudConfigured: Boolean,
    isCloudReachable: Boolean,
) {
    val indicatorColor = when {
        isGenerating -> Gold
        isCloudReachable -> GoldDim
        isCloudConfigured -> GoldSubtle
        else -> DimGrey
    }
    val statusText = when {
        isGenerating -> "The villain is speaking"
        isCloudReachable -> "Signal to the villain is live"
        isCloudConfigured -> "Cloud line armed"
        else -> "Off-grid fallback"
    }
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .clip(shape)
            .background(GlassIvory)
            .border(1.dp, FrostBorder, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "DOOMSY",
            style = MaterialTheme.typography.labelMedium.copy(
                color = GoldDim,
            ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(indicatorColor),
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DeepBlack,
                ),
            )
        }
    }
}

@Composable
private fun UtilityPill(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassIvory)
            .border(1.dp, SoftGoldBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = DeepBlack,
            ),
        )
    }
}

@Composable
private fun MusicTray(
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GlassIvory)
            .border(1.dp, FrostBorder, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "DOOM scriptures",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GoldDim,
                    ),
                )
                Text(
                    text = "Doomsy is but a vessel. DOOM is the scripture.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = StormInk,
                        fontStyle = FontStyle.Italic,
                    ),
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            UtilityPill(
                text = if (expanded) "^" else "v",
                onClick = onToggle,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Column {
                Spacer(modifier = Modifier.size(14.dp))
                TracksCarousel(
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
