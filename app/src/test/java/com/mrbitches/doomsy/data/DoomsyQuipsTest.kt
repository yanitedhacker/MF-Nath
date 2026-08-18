package com.mrbitches.doomsy.data

import org.junit.Assert.assertTrue
import org.junit.Test

class DoomsyQuipsTest {

    @Test
    fun `quip pool has at least 15 quips`() {
        assertTrue(DoomsyQuips.allQuips.size >= 15)
    }

    @Test
    fun `random quip returns a non-empty string`() {
        val quip = DoomsyQuips.random()
        assertTrue(quip.isNotBlank())
    }

    @Test
    fun `random quip is from the pool`() {
        repeat(20) {
            val quip = DoomsyQuips.random()
            assertTrue(
                "Quip not in pool: $quip",
                DoomsyQuips.allQuips.contains(quip),
            )
        }
    }

    @Test
    fun `deep quip pool is non-empty and distinct from tap quips`() {
        assertTrue(DoomsyQuips.deepQuips.isNotEmpty())
        DoomsyQuips.deepQuips.forEach { quip ->
            assertTrue(quip.isNotBlank())
        }
        val deep = DoomsyQuips.randomDeep()
        assertTrue(DoomsyQuips.deepQuips.contains(deep))
    }
}
