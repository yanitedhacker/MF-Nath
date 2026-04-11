package com.mrbitches.doomsy.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.GlassDark
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.GunmetalGrey
import com.mrbitches.doomsy.ui.theme.MutedGrey
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.ui.theme.Obsidian
import com.mrbitches.doomsy.util.Haptic

@Composable
fun ChatPanel(
    visible: Boolean,
    messages: List<Message>,
    isGenerating: Boolean,
    isModelLoading: Boolean,
    isModelLoaded: Boolean,
    onSendMessage: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.75f, stiffness = 250f),
            initialOffsetY = { it },
        ),
        exit = slideOutVertically(
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f),
            targetOffsetY = { it },
        ),
        modifier = modifier,
    ) {
        var dragOffset by remember { mutableFloatStateOf(0f) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.58f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassDark,
                            Obsidian.copy(alpha = 0.97f),
                        ),
                    ),
                )
                .border(
                    width = 0.5.dp,
                    color = GlassWhiteBorder,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                )
                .imePadding(),
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (dragOffset > 80f) onClose()
                                dragOffset = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                if (dragAmount > 0) dragOffset += dragAmount
                            },
                        )
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MutedGrey.copy(alpha = 0.5f)),
                )
            }

            // Model loading indicator
            if (isModelLoading) {
                Text(
                    text = "THE VILLAIN AWAKENS\u2026",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GoldDim,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp),
                )
            }

            // Messages
            val listState = rememberLazyListState()

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(messages, key = { it.timestamp }) { message ->
                    ChatBubble(
                        message = message,
                        animate = !message.isUser && message == messages.lastOrNull(),
                    )
                }

                if (isGenerating) {
                    item {
                        Text(
                            text = "The villain contemplates\u2026",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Gold.copy(alpha = 0.5f),
                            ),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(4.dp)) }
            }

            // Input bar
            ChatInputBar(
                onSend = { text ->
                    Haptic.tap(context)
                    onSendMessage(text)
                },
                enabled = !isGenerating,
            )

            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(4.dp),
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    onSend: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoiceInputButton(
            onResult = { spoken ->
                if (spoken.isNotBlank()) {
                    onSend(spoken)
                }
            },
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = OffWhite),
            cursorBrush = SolidColor(Gold),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(DeepBlack.copy(alpha = 0.6f))
                .border(
                    width = 0.5.dp,
                    color = GlassWhiteBorder,
                    shape = RoundedCornerShape(22.dp),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = "Talk to Doomsy\u2026",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MutedGrey,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )

        Spacer(modifier = Modifier.width(8.dp))

        val canSend = text.isNotBlank() && enabled
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (canSend) Gold else MutedGrey.copy(alpha = 0.2f))
                .then(
                    if (canSend) Modifier.clickable {
                        onSend(text)
                        text = ""
                    } else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u25B6",
                style = MaterialTheme.typography.labelLarge.copy(color = DeepBlack),
            )
        }
    }
}
