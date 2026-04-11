package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrbitches.doomsy.ui.chat.ChatPanel
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.DimGrey
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.GoldSubtle
import com.mrbitches.doomsy.ui.theme.MutedGrey
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.ui.theme.Void

@Composable
fun MainScreen(viewModel: DoomsyViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isModelLoading by viewModel.isModelLoading.collectAsState()
    val isModelLoaded by viewModel.isModelLoaded.collectAsState()
    val currentQuip by viewModel.currentQuip.collectAsState()
    val isChatOpen by viewModel.isChatOpen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void),
    ) {
        // 3D character fills entire background
        DoomsyViewer(
            modifier = Modifier.fillMaxSize(),
            onTap = {
                if (!isChatOpen) viewModel.triggerQuip()
            },
            onLongPress = {},
            scaledDown = isChatOpen,
        )

        // Top gradient fade for status bar readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Void.copy(alpha = 0.7f),
                            Void.copy(alpha = 0f),
                        ),
                    ),
                ),
        )

        // Bottom gradient fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Void.copy(alpha = 0f),
                            Void.copy(alpha = 0.85f),
                            Void,
                        ),
                    ),
                ),
        )

        // Top status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "DOOMSY",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isModelLoaded) Gold
                                else if (isModelLoading) GoldDim
                                else DimGrey,
                            ),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isModelLoaded -> "The villain is awake"
                            isModelLoading -> "Awakening..."
                            else -> "Dormant"
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // Quip overlay (centered on character)
        QuipOverlay(
            quip = currentQuip,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            onDismissed = { viewModel.dismissQuip() },
        )

        // Bottom content -- only visible when chat is closed
        AnimatedVisibility(
            visible = !isChatOpen,
            enter = fadeIn(spring()),
            exit = fadeOut(spring()),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Doomsy is but a vessel. DOOM is the scripture.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GoldDim,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(horizontal = 40.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                TracksCarousel(
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Chat open button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Gold)
                        .clickable { viewModel.openChat() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u2026",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = DeepBlack,
                        ),
                    )
                }
            }
        }

        // Chat bottom sheet
        ChatPanel(
            visible = isChatOpen,
            messages = messages,
            isGenerating = isGenerating,
            isModelLoading = isModelLoading,
            isModelLoaded = isModelLoaded,
            onSendMessage = { viewModel.sendMessage(it) },
            onClose = { viewModel.closeChat() },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
