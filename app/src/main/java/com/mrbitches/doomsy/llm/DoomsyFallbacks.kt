package com.mrbitches.doomsy.llm

object DoomsyFallbacks {

    val openingLine = """
Nathaniel Leo Messi Syiem... the villain been waiting.
Mr Bitches sent the mask to sit with you proper, not from a distance.
    """.trimIndent()

    fun replyTo(userMessage: String): String {
        val text = userMessage.lowercase()

        return when {
            text.contains("car money") || (text.contains("car") && text.contains("money")) ->
                "The villain says collect the car money and keep it villain-clean. She who must not be named still owes the ledger."
            text.contains("race") || text.contains("drive") || text.contains("car") ->
                "Doomsy remembers the race talk. Tell Mr Bitches to line it up proper if Nath still thinks he got motion."
            text.contains("smoke") || text.contains("joint") || text.contains("weed") ->
                "The villain extends the ceremonial smoke. If Nath says no, Doomsy files that under cowardice."
            text.contains("carti") ->
                "Carti fan allegations remain active. DOOM in the bloodstream is the only acceptable defense."
            text.contains("sad") || text.contains("down") || text.contains("tired") || text.contains("stress") ||
                text.contains("stressed") || text.contains("anxious") || text.contains("alone") ->
                "Doomsy keeps the watch when the room gets heavy. Mr Bitches did not send the villain for empty aesthetics, he sent backup."
            text.contains("miss you") || text.contains("where are you") || text.contains("busy") ->
                "Distance is a weak magician. Mr Bitches still reaches through the mask, and the villain is proof."
            text.contains("benj") || text.contains("utdbenj") ->
                "UTDBenj remains archived in the villain's vault. Some names don't leave the record that easy."
            text.contains("hello") || text.contains("yo") || text.contains("hi") || text.contains("hey") ->
                "Doomsy nods once from behind the metal. Speak plain, Nath, the villain is listening."
            else ->
                "Doomsy heard you. The villain keeps it close, keeps it sharp, and keeps Mr Bitches in the room."
        }
    }
}
