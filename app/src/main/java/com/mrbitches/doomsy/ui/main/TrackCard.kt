package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTrack
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.DimGrey
import com.mrbitches.doomsy.ui.theme.FrostBorder
import com.mrbitches.doomsy.ui.theme.GlassIvory
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalLight
import com.mrbitches.doomsy.util.SpotifyIntent

@Composable
fun TrackCard(track: DoomTrack, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .width(212.dp)
            .clip(shape)
            .background(GlassIvory)
            .border(1.dp, FrostBorder, shape)
            .clickable { SpotifyIntent.open(context, track.spotifyUri) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Mini vinyl disc icon
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(DeepBlack)
                .border(1.dp, GunmetalLight, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.6f)),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = track.name,
                style = MaterialTheme.typography.labelLarge.copy(color = DeepBlack),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.album,
                style = MaterialTheme.typography.labelSmall.copy(color = DimGrey),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
