package com.mrbitches.doomsy.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassStark
import com.mrbitches.doomsy.ui.theme.GlassSmoke
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.ui.theme.SoftOrangeBorder

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.isUser

    val shape = RoundedCornerShape(
        topStart = 22.dp,
        topEnd = 22.dp,
        bottomStart = if (isUser) 22.dp else 10.dp,
        bottomEnd = if (isUser) 10.dp else 22.dp,
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 272.dp)
                .clip(shape)
                .background(
                    if (isUser) GlassSmoke
                    else GlassStark,
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) FrostBorder else SoftOrangeBorder,
                    shape = shape,
                )
                .semantics {
                    contentDescription = if (isUser) "Your message" else "Doomsy message"
                }
                .padding(horizontal = 15.dp, vertical = 11.dp),
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isUser) OffWhite else DeepBlack,
                ),
            )
        }
    }
}
