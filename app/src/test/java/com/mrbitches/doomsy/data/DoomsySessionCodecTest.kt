package com.mrbitches.doomsy.data

import com.mrbitches.doomsy.llm.Exchange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DoomsySessionCodecTest {

    @Test
    fun `messages round-trip through storage format`() {
        val messages = listOf(
            Message(text = "hello villain", isUser = true, timestamp = 11L, animateReveal = true),
            Message(text = "The mask turns.", isUser = false, timestamp = 12L, animateReveal = true),
        )

        val restored = DoomsySessionCodec.decodeMessages(DoomsySessionCodec.encodeMessages(messages))

        assertEquals(2, restored.size)
        assertEquals("hello villain", restored[0].text)
        assertTrue(restored[0].isUser)
        assertEquals(11L, restored[0].timestamp)
        assertFalse("Restored messages should not re-animate", restored[1].animateReveal)
        assertEquals("The mask turns.", restored[1].text)
    }

    @Test
    fun `messages preserve newlines and tabs`() {
        val messages = listOf(
            Message(text = "line one\nline two\tstill here", isUser = true, timestamp = 7L),
        )

        val restored = DoomsySessionCodec.decodeMessages(DoomsySessionCodec.encodeMessages(messages))

        assertEquals("line one\nline two\tstill here", restored.single().text)
    }

    @Test
    fun `decodeMessages returns empty for blank or invalid lines`() {
        assertTrue(DoomsySessionCodec.decodeMessages("").isEmpty())
        assertTrue(DoomsySessionCodec.decodeMessages("not-a-record").isEmpty())
    }

    @Test
    fun `encodeMessages keeps only the newest cap`() {
        val messages = (1..45).map { index ->
            Message(text = "msg $index", isUser = index % 2 == 0, timestamp = index.toLong())
        }

        val restored = DoomsySessionCodec.decodeMessages(DoomsySessionCodec.encodeMessages(messages))

        assertEquals(DoomsySessionCodec.MAX_STORED_MESSAGES, restored.size)
        assertEquals("msg 6", restored.first().text)
        assertEquals("msg 45", restored.last().text)
    }

    @Test
    fun `history round-trip through storage format`() {
        val history = listOf(
            Exchange("yo", "Doomsy nods."),
            Exchange("car money", "Collect it."),
        )

        val restored = DoomsySessionCodec.decodeHistory(DoomsySessionCodec.encodeHistory(history))

        assertEquals(2, restored.size)
        assertEquals("yo", restored[0].userMessage)
        assertEquals("Collect it.", restored[1].assistantResponse)
    }

    @Test
    fun `decodeHistory skips incomplete exchanges`() {
        val restored = DoomsySessionCodec.decodeHistory("only user\nok\treply")

        assertEquals(1, restored.size)
        assertEquals("ok", restored[0].userMessage)
        assertEquals("reply", restored[0].assistantResponse)
    }
}
