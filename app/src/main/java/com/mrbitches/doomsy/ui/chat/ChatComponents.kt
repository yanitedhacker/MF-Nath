package com.mrbitches.doomsy.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassSmoke
import com.mrbitches.doomsy.ui.theme.GlassStark
import com.mrbitches.doomsy.ui.theme.MistGrey
import com.mrbitches.doomsy.ui.theme.MutedGrey
import com.mrbitches.doomsy.ui.theme.SoftOrangeBorder
import com.mrbitches.doomsy.ui.theme.VillainOrange
import com.mrbitches.doomsy.ui.theme.VillainOrangeDim
import com.mrbitches.doomsy.util.Haptic

@Composable
fun ChatMessageStack(
    messages: List<Message>,
    isGenerating: Boolean,
    modifier: Modifier = Modifier,
    maxVisible: Int = 12,
) {
    val visibleMessages = messages.takeLast(maxVisible)
    val listState = rememberLazyListState()

    LaunchedEffect(visibleMessages.size, isGenerating, visibleMessages.lastOrNull()?.text) {
        val lastIndex = visibleMessages.lastIndex + if (isGenerating) 1 else 0
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier
            .widthIn(max = 320.dp)
            .heightIn(max = 280.dp)
            .semantics { contentDescription = "Chat with Doomsy" },
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        itemsIndexed(
            visibleMessages,
            key = { index, message -> "${message.timestamp}-${message.isUser}-$index" },
        ) { _, message ->
            ChatBubble(
                message = message,
                animate = message.animateReveal && !message.isUser && message == messages.lastOrNull(),
            )
        }

        if (isGenerating) {
            item(key = "typing") {
                TypingChip()
            }
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
    val context = LocalContext.current

    fun submit() {
        if (!canSend) return
        Haptic.tap(context)
        onSend(text.trim())
        text = ""
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(GlassStark)
            .border(1.dp, SoftOrangeBorder, shape)
            .padding(horizontal = 16.dp, vertical = 16.dp),
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
            cursorBrush = SolidColor(VillainOrange),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { submit() }),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .semantics { contentDescription = "Message Doomsy" },
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
                .semantics { contentDescription = "Send message" }
                .then(
                    if (canSend) {
                        Modifier.clickable { submit() }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = if (canSend) VillainOrange else MutedGrey,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun ChatSignalChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassStark)
            .border(1.dp, FrostBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = VillainOrangeDim,
            ),
        )
    }
}

@Composable
private fun TypingChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassStark)
            .border(1.dp, SoftOrangeBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .semantics { contentDescription = "Doomsy is composing a reply" },
    ) {
        Text(
            text = "Doomsy tunes the static.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = DeepBlack,
            ),
        )
    }
}
