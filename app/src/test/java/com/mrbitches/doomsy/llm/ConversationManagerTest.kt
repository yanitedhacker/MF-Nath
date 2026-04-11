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
    fun `history starts empty`() {
        assertTrue(manager.history.isEmpty())
    }

    @Test
    fun `addExchange stores messages`() {
        manager.addExchange("hello", "The villain greets you.")
        assertEquals(1, manager.history.size)
        assertEquals("hello", manager.history.first().userMessage)
        assertEquals("The villain greets you.", manager.history.first().assistantResponse)
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
    fun `custom exchange cap is respected`() {
        val customManager = ConversationManager(maxExchanges = 2)

        customManager.addExchange("first", "one")
        customManager.addExchange("second", "two")
        customManager.addExchange("third", "three")

        assertEquals(2, customManager.history.size)
        assertEquals("second", customManager.history.first().userMessage)
        assertEquals("third", customManager.history.last().userMessage)
    }

    @Test
    fun `clear resets history`() {
        manager.addExchange("hello", "world")
        manager.clear()
        assertTrue(manager.history.isEmpty())
    }
}
