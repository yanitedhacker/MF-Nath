package com.mrbitches.doomsy.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassIvory
import com.mrbitches.doomsy.ui.theme.GlassSmoke
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.MistGrey
import com.mrbitches.doomsy.ui.theme.MutedGrey
import com.mrbitches.doomsy.ui.theme.SoftGoldBorder

@Composable
fun ChatMessageStack(
    messages: List<Message>,
    isGenerating: Boolean,
    modifier: Modifier = Modifier,
    maxVisible: Int = 4,
) {
    val visibleMessages = messages.takeLast(maxVisible)

    Column(
        modifier = modifier.widthIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End,
    ) {
        visibleMessages.forEach { message ->
            ChatBubble(
                message = message,
                animate = !message.isUser && message == messages.lastOrNull(),
            )
        }

        if (isGenerating) {
            TypingChip()
        }
    }
}

@Composable
fun ChatComposer(
    onSend: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }
    val canSend = enabled && text.isNotBlank()
    val shape = RoundedCornerShape(30.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(GlassIvory)
            .border(1.dp, SoftGoldBorder, shape)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoiceInputButton(
            onResult = { spoken ->
                if (spoken.isNotBlank()) {
                    onSend(spoken)
                }
            },
        )

        Spacer(modifier = Modifier.width(10.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            enabled = enabled,
            singleLine = false,
            maxLines = 4,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = DeepBlack),
            cursorBrush = SolidColor(Gold),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = "Talk to Doomsy",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MistGrey,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (canSend) DeepBlack else GlassSmoke)
                .then(
                    if (canSend) {
                        Modifier.clickable {
                            onSend(text.trim())
                            text = ""
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = ">",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (canSend) Gold else MutedGrey,
                ),
            )
        }
    }
}

@Composable
fun ChatSignalChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassIvory)
            .border(1.dp, FrostBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = GoldDim,
            ),
        )
    }
}

@Composable
private fun TypingChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassIvory)
            .border(1.dp, SoftGoldBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
    ) {
        Text(
            text = "Doomsy tunes the static.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = DeepBlack,
            ),
        )
    }
}
