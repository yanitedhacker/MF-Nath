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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalLight
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Anim
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(message: Message, animate: Boolean = false) {
    val isUser = message.isUser

    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp,
    )

    var visibleChars by remember(message.text) { mutableIntStateOf(if (animate) 0 else message.text.length) }

    if (animate && visibleChars < message.text.length) {
        LaunchedEffect(message.text) {
            while (visibleChars < message.text.length) {
                delay(Anim.TYPEWRITER_CHAR_DELAY)
                visibleChars++
            }
        }
    }

    val displayText = message.text.take(visibleChars)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(if (isUser) GunmetalLight else GlassWhite)
                .border(
                    width = 0.5.dp,
                    color = if (isUser) GlassWhite else Gold.copy(alpha = 0.3f),
                    shape = shape,
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge.copy(color = OffWhite),
            )
        }
    }
}
