package com.mrbitches.doomsy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatExportTest {

    @Test
    fun `empty transcript has a header`() {
        val exported = ChatExport.format(emptyList())
        assertTrue(exported.startsWith("Doomsy chat"))
        assertTrue(exported.contains("(empty)"))
    }

    @Test
    fun `roles are labeled for share sheets`() {
        val exported = ChatExport.format(
            listOf(
                Message(text = "yo", isUser = true, timestamp = 1L),
                Message(text = "The mask turns.", isUser = false, timestamp = 2L),
            ),
        )

        assertTrue(exported.contains("[You]"))
        assertTrue(exported.contains("yo"))
        assertTrue(exported.contains("[Doomsy]"))
        assertTrue(exported.contains("The mask turns."))
        assertEquals(
            "Doomsy chat\n===========\n\n[You]\nyo\n\n[Doomsy]\nThe mask turns.",
            exported,
        )
    }
}
