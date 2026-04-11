package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrbitches.doomsy.ui.chat.ChatPanel
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalGrey

@Composable
fun MainScreen(viewModel: DoomsyViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentQuip by viewModel.currentQuip.collectAsState()
    val isChatOpen by viewModel.isChatOpen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlack, GunmetalGrey, DeepBlack),
                ),
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isChatOpen) 0.4f else 0.6f),
            ) {
                DoomsyViewer(
                    modifier = Modifier.fillMaxSize(),
                    onTap = {
                        if (!isChatOpen) viewModel.triggerQuip()
                    },
                    onLongPress = {},
                    scaledDown = isChatOpen,
                )

                QuipOverlay(
                    quip = currentQuip,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    onDismissed = { viewModel.dismissQuip() },
                )
            }

            if (!isChatOpen) {
                TracksCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Gold)
                        .clickable { viewModel.openChat() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u2026",
                        style = MaterialTheme.typography.headlineMedium.copy(color = DeepBlack),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        ChatPanel(
            visible = isChatOpen,
            messages = messages,
            isGenerating = isGenerating,
            onSendMessage = { viewModel.sendMessage(it) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
