package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTracks

@Composable
fun TracksCarousel(modifier: Modifier = Modifier) {
    val tracks = remember { DoomTracks.randomSelection(10) }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tracks, key = { it.spotifyUri }) { track ->
            TrackCard(track = track)
        }
    }
}
