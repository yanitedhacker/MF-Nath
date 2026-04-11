package com.mrbitches.doomsy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DoomTracksTest {

    @Test
    fun `track pool has at least 25 songs`() {
        assertTrue(DoomTracks.allTracks.size >= 25)
    }

    @Test
    fun `randomSelection returns exactly 10 tracks`() {
        val selection = DoomTracks.randomSelection()
        assertEquals(10, selection.size)
    }

    @Test
    fun `randomSelection returns no duplicates`() {
        val selection = DoomTracks.randomSelection()
        assertEquals(selection.size, selection.toSet().size)
    }

    @Test
    fun `every track has a non-empty name and album`() {
        DoomTracks.allTracks.forEach { track ->
            assertTrue("Track name is empty", track.name.isNotBlank())
            assertTrue("Album is empty for ${track.name}", track.album.isNotBlank())
        }
    }

    @Test
    fun `every track has a spotify URI`() {
        DoomTracks.allTracks.forEach { track ->
            assertTrue(
                "Missing Spotify URI for ${track.name}",
                track.spotifyUri.startsWith("spotify:track:") ||
                    track.spotifyUri.startsWith("https://open.spotify.com/track/"),
            )
        }
    }
}
