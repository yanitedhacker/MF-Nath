package com.mrbitches.doomsy.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object SpotifyIntent {

    fun buildUri(spotifyUri: String): String {
        if (spotifyUri.startsWith("https://")) return spotifyUri
        val parts = spotifyUri.removePrefix("spotify:").split(":")
        if (parts.size == 2) {
            return "https://open.spotify.com/${parts[0]}/${parts[1]}"
        }
        return spotifyUri
    }

    fun open(context: Context, spotifyUri: String) {
        val url = buildUri(spotifyUri)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.mrbitches.doomsy"))
        }
        intent.setPackage("com.spotify.music")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }
}
