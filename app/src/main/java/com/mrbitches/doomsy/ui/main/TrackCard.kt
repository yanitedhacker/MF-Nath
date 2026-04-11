package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTrack
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.SpotifyIntent

@Composable
fun TrackCard(track: DoomTrack, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(GlassWhite)
            .border(1.dp, GlassWhiteBorder, shape)
            .clickable { SpotifyIntent.open(context, track.spotifyUri) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.labelLarge.copy(color = OffWhite),
            maxLines = 1,
        )
        Text(
            text = track.album,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}
