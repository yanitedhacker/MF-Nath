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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrbitches.doomsy.ui.chat.ChatComposer
import com.mrbitches.doomsy.ui.chat.ChatMessageStack
import com.mrbitches.doomsy.ui.chat.ChatSignalChip
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.DimGrey
import com.mrbitches.doomsy.ui.theme.AshGrey
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassStark
import com.mrbitches.doomsy.ui.theme.VillainOrange
import com.mrbitches.doomsy.ui.theme.VillainOrangeDim
import com.mrbitches.doomsy.ui.theme.VillainOrangeSubtle
import com.mrbitches.doomsy.ui.theme.StarkGrey
import com.mrbitches.doomsy.ui.theme.StarkWhite
import com.mrbitches.doomsy.ui.theme.SoftOrangeBorder
import com.mrbitches.doomsy.ui.theme.StormInk

@Composable
fun MainScreen(viewModel: DoomsyViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isCloudConfigured by viewModel.isCloudConfigured.collectAsStateWithLifecycle()
    val isCloudReachable by viewModel.isCloudReachable.collectAsStateWithLifecycle()
    val currentQuip by viewModel.currentQuip.collectAsStateWithLifecycle()
    var isMusicExpanded by rememberSaveable { mutableStateOf(false) }
    var trackShuffleKey by remember { mutableIntStateOf(0) }
    val toggleMusicTray = {
        if (!isMusicExpanded) trackShuffleKey++
        isMusicExpanded = !isMusicExpanded
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StarkGrey),
    ) {
        AtmosphericBackdrop()

        DoomsyViewer(
            modifier = Modifier.fillMaxSize(),
            onTap = { viewModel.triggerQuip() },
            onLongPress = { viewModel.triggerDeepQuip() },
            scaleToUnits = 0.72f,
            verticalOffset = 0.03f,
            horizontalOffset = -0.14f,
            idleRotationSpan = 3.6f,
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
                .padding(horizontal = 24.dp, vertical = 24.dp),
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
                    onClick = toggleMusicTray,
                    contentDescription = if (isMusicExpanded) {
                        "Hide track carousel"
                    } else {
                        "Show shuffled tracks"
                    },
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
                trackShuffleKey = trackShuffleKey,
                onToggle = toggleMusicTray,
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
                        colors = listOf(StarkWhite, StarkGrey, AshGrey),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            StarkWhite.copy(alpha = 0.8f),
                            Color.Transparent,
                        ),
                        radius = 1250f,
                    ),
                ),
        )
        
        // Remove the vertical gradient overlay that was washing out the bottom
    }
}

@Composable
private fun StatusCluster(
    isGenerating: Boolean,
    isCloudConfigured: Boolean,
    isCloudReachable: Boolean,
) {
    val indicatorColor = when {
        isGenerating -> VillainOrange
        isCloudReachable -> VillainOrangeDim
        isCloudConfigured -> VillainOrangeSubtle
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
            .background(GlassStark)
            .border(1.dp, FrostBorder, shape)
            .semantics { contentDescription = statusText }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "DOOMSY",
            style = MaterialTheme.typography.labelMedium.copy(
                color = VillainOrangeDim,
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
    contentDescription: String = text,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassStark)
            .border(1.dp, SoftOrangeBorder, RoundedCornerShape(18.dp))
            .semantics { this.contentDescription = contentDescription }
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
    trackShuffleKey: Int,
    onToggle: () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GlassStark)
            .border(1.dp, FrostBorder, shape)
            .padding(horizontal = 24.dp, vertical = 24.dp),
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
                        color = VillainOrangeDim,
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
                contentDescription = if (expanded) {
                    "Collapse track carousel"
                } else {
                    "Expand track carousel"
                },
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
                    shuffleKey = trackShuffleKey,
                )
            }
        }
    }
}
