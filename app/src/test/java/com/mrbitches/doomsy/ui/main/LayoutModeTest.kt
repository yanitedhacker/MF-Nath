package com.mrbitches.doomsy.ui.main

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutModeTest {

    @Test
    fun `phone portrait stays single pane`() {
        assertFalse(shouldUseTwoPane(widthDp = 411, heightDp = 891))
    }

    @Test
    fun `phone landscape uses two panes`() {
        assertTrue(shouldUseTwoPane(widthDp = 891, heightDp = 411))
    }

    @Test
    fun `tablet width uses two panes even in portrait`() {
        assertTrue(shouldUseTwoPane(widthDp = 800, heightDp = 1280))
    }

    @Test
    fun `narrow landscape stays stacked`() {
        assertFalse(shouldUseTwoPane(widthDp = 400, heightDp = 360))
    }
}
