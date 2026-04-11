package com.mrbitches.doomsy.llm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConversationManagerTest {

    private lateinit var manager: ConversationManager

    @Before
    fun setup() {
        manager = ConversationManager()
    }

    @Test
    fun `buildPrompt includes system prompt`() {
        val prompt = manager.buildPrompt("hello")
        assertTrue(prompt.contains("You are Doomsy"))
    }

    @Test
    fun `buildPrompt includes user message`() {
        val prompt = manager.buildPrompt("yo what's up")
        assertTrue(prompt.contains("yo what's up"))
    }

    @Test
    fun `addExchange stores messages`() {
        manager.addExchange("hello", "The villain greets you.")
        assertEquals(1, manager.history.size)
    }

    @Test
    fun `history caps at 10 exchanges`() {
        repeat(12) { i ->
            manager.addExchange("msg $i", "response $i")
        }
        assertEquals(10, manager.history.size)
        assertEquals("msg 2", manager.history.first().userMessage)
    }

    @Test
    fun `buildPrompt includes conversation history`() {
        manager.addExchange("first", "response one")
        val prompt = manager.buildPrompt("second")
        assertTrue(prompt.contains("first"))
        assertTrue(prompt.contains("response one"))
        assertTrue(prompt.contains("second"))
    }

    @Test
    fun `clear resets history`() {
        manager.addExchange("hello", "world")
        manager.clear()
        assertTrue(manager.history.isEmpty())
    }
}
