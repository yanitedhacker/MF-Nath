package com.mrbitches.doomsy.data

data class DoomTrack(
    val name: String,
    val artist: String,
    val album: String,
    val spotifyUri: String,
)

object DoomTracks {

    val allTracks = listOf(
        // MF DOOM & Madvillain
        DoomTrack("Doomsday", "MF DOOM", "Operation: Doomsday", "spotify:track:7i09RLbBT8m0LvH2dYiJqp"),
        DoomTrack("Rhymes Like Dimes", "MF DOOM", "Operation: Doomsday", "spotify:track:1FaFSBnUbSibDFMOgqiMJv"),
        DoomTrack("Gas Drawls", "MF DOOM", "Operation: Doomsday", "spotify:track:5UWGB7yZHJWdX0lVNMHcMo"),
        DoomTrack("Hey!", "MF DOOM", "Operation: Doomsday", "spotify:track:4v1kxJ68VPJelE6IJkrLaF"),
        DoomTrack("Books of War", "MF DOOM", "Operation: Doomsday", "spotify:track:5tGCaJzSCINP6ylfMHvJcP"),
        DoomTrack("Accordion", "Madvillain", "Madvillainy", "spotify:track:1FDcMuwdJD1nan1HKBM71I"),
        DoomTrack("All Caps", "Madvillain", "Madvillainy", "spotify:track:6lDHbMO3SQGBPO3RCJN1IH"),
        DoomTrack("Meat Grinder", "Madvillain", "Madvillainy", "spotify:track:3RfCpX9VYqvGTGLnjhnYMK"),
        DoomTrack("Figaro", "Madvillain", "Madvillainy", "spotify:track:5E6fFkDaG2YRy8P7ByYejz"),
        DoomTrack("Rhinestone Cowboy", "Madvillain", "Madvillainy", "spotify:track:3Oq76sPHimNWElIiByTawL"),
        DoomTrack("Curls", "Madvillain", "Madvillainy", "spotify:track:30MUDf98bwJGiC2FswzHPb"),
        DoomTrack("Raid", "Madvillain", "Madvillainy", "spotify:track:5YwGBbbtHElFp4yMSRTMFT"),
        DoomTrack("Strange Ways", "Madvillain", "Madvillainy", "spotify:track:0FBnpJTU3lBkfxH6abKq5d"),
        DoomTrack("Rapp Snitch Knishes", "MF DOOM", "MM..FOOD", "spotify:track:55fmthmn3rgnk9Wyx7G5dU"),
        DoomTrack("One Beer", "MF DOOM", "MM..FOOD", "spotify:track:10JnMkMuaAqGHqNhJJEeJl"),
        DoomTrack("Beef Rapp", "MF DOOM", "MM..FOOD", "spotify:track:3lHEvvODyyQccbSGiEylOJ"),
        DoomTrack("Potholderz", "MF DOOM", "MM..FOOD", "spotify:track:79JlOHhFRHNMByxZNEhgKK"),
        DoomTrack("Hoe Cakes", "MF DOOM", "MM..FOOD", "spotify:track:1F1XSEL65Kbv3YJLahPZq0"),
        DoomTrack("Vomitspit", "MF DOOM", "Vaudeville Villain", "spotify:track:22CXDwIlSUFZffl9SiMQqE"),
        DoomTrack("Lickupon", "MF DOOM", "Vaudeville Villain", "spotify:track:3XUxIHEhNegK30SzMiOj0j"),
        DoomTrack("Let Me Watch", "MF DOOM", "Vaudeville Villain", "spotify:track:5HQVzoat4MjVFmu1PKI8fA"),
        DoomTrack("That's That", "MF DOOM", "Born Like This", "spotify:track:3xNI3vWi0d7oCSJ8YOuKNb"),
        DoomTrack("Cellz", "MF DOOM", "Born Like This", "spotify:track:4gg1qXIYS0bXGtIW2LWjWr"),
        DoomTrack("Gazzillion Ear", "MF DOOM", "Born Like This", "spotify:track:0J4p8UiLMhfdPqLjVYYpZ4"),
        DoomTrack("Kon Karne", "MF DOOM", "Take Me to Your Leader", "spotify:track:3d2cNfDC1ax5m2MpxXE8bJ"),

        // Frank Ocean
        DoomTrack("Nights", "Frank Ocean", "Blonde", "spotify:track:7eqZUDRtZKmL4Vpk36SJNB"),
        DoomTrack("Self Control", "Frank Ocean", "Blonde", "spotify:track:7A7y1q52Bbm0OIlzUPKt4t"),
        DoomTrack("Pink + White", "Frank Ocean", "Blonde", "spotify:track:3xKsf9qdS1YvIJbmKctdV6"),
        DoomTrack("Ivy", "Frank Ocean", "Blonde", "spotify:track:2nPvwKYtLJKtZopVAYa21G"),
        DoomTrack("Thinkin Bout You", "Frank Ocean", "Channel Orange", "spotify:track:7DfFc7d6Nwi9nGtBW6n2E3"),

        // Westside Gunn & Griselda
        DoomTrack("DR. BIRDS", "Westside Gunn", "WWCD", "spotify:track:77EseIwL4dnUACjF3YmDWz"),
        DoomTrack("George Bondo", "Westside Gunn", "Pray for Paris", "spotify:track:2TMmxS4qWwfZ5h2xwaDFsY"),
        DoomTrack("Michael Irvin", "Westside Gunn", "Pray for Paris", "spotify:track:6BZFzi6roce3ZBQ6dq13f7"),

        // Earl Sweatshirt
        DoomTrack("Chum", "Earl Sweatshirt", "Doris", "spotify:track:6plT7nFGiXKSBP9HFSI4ef"),
        DoomTrack("EAST", "Earl Sweatshirt", "FEET OF CLAY", "spotify:track:5D3AABFaH7f1XVN8zAiX3H"),

        // Freddie Gibbs & Madlib, Tyler, Benny, Boldy, Mach-Hommy
        DoomTrack("Thuggin'", "Freddie Gibbs & Madlib", "Piñata", "spotify:track:4XS8vumM5v1KEeZC4EvMqQ"),
        DoomTrack("EARFQUAKE", "Tyler, The Creator", "IGOR", "spotify:track:5hVghJ4KaYES3BFUATCYn0"),
        DoomTrack("Crowns for Kings", "Benny The Butcher", "The Plugs I Met", "spotify:track:0FQeZmAKQLLnZC5iTN6JuR"),
        DoomTrack("Scrape the Bowl", "Boldy James & The Alchemist", "The Price of Tea in China (Deluxe)", "spotify:track:1NjgNWzURicunzJ4K2kOvt"),
        DoomTrack("No Blood No Sweat", "Mach-Hommy", "Pray for Haiti", "spotify:track:4DCZxox8ZvICoaG02cxHnj"),
    )

    fun randomSelection(count: Int = 10): List<DoomTrack> {
        return allTracks.shuffled().take(count)
    }
}
