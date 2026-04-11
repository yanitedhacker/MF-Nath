package com.mrbitches.doomsy.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyIntentTest {

    @Test
    fun `buildUri converts spotify URI to content link`() {
        val uri = SpotifyIntent.buildUri("spotify:track:55fmthmn3rgnk9Wyx7G5dU")
        assertEquals("https://open.spotify.com/track/55fmthmn3rgnk9Wyx7G5dU", uri)
    }

    @Test
    fun `buildUri passes through https links unchanged`() {
        val url = "https://open.spotify.com/track/55fmthmn3rgnk9Wyx7G5dU"
        val uri = SpotifyIntent.buildUri(url)
        assertEquals(url, uri)
    }
}
