package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTracks
import com.mrbitches.doomsy.ui.theme.Gold

@Composable
fun TracksCarousel(modifier: Modifier = Modifier) {
    val tracks = remember { DoomTracks.randomSelection() }

    Column(modifier = modifier) {
        Text(
            text = "Doomsy is but a vessel. DOOM is the scripture.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Gold,
                fontStyle = FontStyle.Italic,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(tracks, key = { it.spotifyUri }) { track ->
                TrackCard(track = track)
            }
        }
    }
}
