package com.mrbitches.doomsy.data

data class DoomTrack(
    val name: String,
    val album: String,
    val spotifyUri: String,
)

object DoomTracks {

    val allTracks = listOf(
        DoomTrack("Doomsday", "Operation: Doomsday", "spotify:track:7i09RLbBT8m0LvH2dYiJqp"),
        DoomTrack("Rhymes Like Dimes", "Operation: Doomsday", "spotify:track:1FaFSBnUbSibDFMOgqiMJv"),
        DoomTrack("Gas Drawls", "Operation: Doomsday", "spotify:track:5UWGB7yZHJWdX0lVNMHcMo"),
        DoomTrack("Hey!", "Operation: Doomsday", "spotify:track:4v1kxJ68VPJelE6IJkrLaF"),
        DoomTrack("Books of War", "Operation: Doomsday", "spotify:track:5tGCaJzSCINP6ylfMHvJcP"),
        DoomTrack("Accordion", "Madvillainy", "spotify:track:1FDcMuwdJD1nan1HKBM71I"),
        DoomTrack("All Caps", "Madvillainy", "spotify:track:6lDHbMO3SQGBPO3RCJN1IH"),
        DoomTrack("Meat Grinder", "Madvillainy", "spotify:track:3RfCpX9VYqvGTGLnjhnYMK"),
        DoomTrack("Figaro", "Madvillainy", "spotify:track:5E6fFkDaG2YRy8P7ByYejz"),
        DoomTrack("Rhinestone Cowboy", "Madvillainy", "spotify:track:3Oq76sPHimNWElIiByTawL"),
        DoomTrack("Curls", "Madvillainy", "spotify:track:30MUDf98bwJGiC2FswzHPb"),
        DoomTrack("Raid", "Madvillainy", "spotify:track:5YwGBbbtHElFp4yMSRTMFT"),
        DoomTrack("Strange Ways", "Madvillainy", "spotify:track:0FBnpJTU3lBkfxH6abKq5d"),
        DoomTrack("Rapp Snitch Knishes", "MM..FOOD", "spotify:track:55fmthmn3rgnk9Wyx7G5dU"),
        DoomTrack("One Beer", "MM..FOOD", "spotify:track:10JnMkMuaAqGHqNhJJEeJl"),
        DoomTrack("Beef Rapp", "MM..FOOD", "spotify:track:3lHEvvODyyQccbSGiEylOJ"),
        DoomTrack("Potholderz", "MM..FOOD", "spotify:track:79JlOHhFRHNMByxZNEhgKK"),
        DoomTrack("Hoe Cakes", "MM..FOOD", "spotify:track:1F1XSEL65Kbv3YJLahPZq0"),
        DoomTrack("Vomitspit", "Vaudeville Villain", "spotify:track:22CXDwIlSUFZffl9SiMQqE"),
        DoomTrack("Lickupon", "Vaudeville Villain", "spotify:track:3XUxIHEhNegK30SzMiOj0j"),
        DoomTrack("Let Me Watch", "Vaudeville Villain", "spotify:track:5HQVzoat4MjVFmu1PKI8fA"),
        DoomTrack("That's That", "Born Like This", "spotify:track:3xNI3vWi0d7oCSJ8YOuKNb"),
        DoomTrack("Cellz", "Born Like This", "spotify:track:4gg1qXIYS0bXGtIW2LWjWr"),
        DoomTrack("Gazzillion Ear", "Born Like This", "spotify:track:0J4p8UiLMhfdPqLjVYYpZ4"),
        DoomTrack("Kon Karne", "Take Me to Your Leader", "spotify:track:3d2cNfDC1ax5m2MpxXE8bJ"),
    )

    fun randomSelection(count: Int = 10): List<DoomTrack> {
        return allTracks.shuffled().take(count)
    }
}
