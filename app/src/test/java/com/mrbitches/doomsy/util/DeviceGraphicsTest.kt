package com.mrbitches.doomsy.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceGraphicsTest {

    @Test
    fun `low-RAM devices skip the GLB`() {
        assertEquals(GraphicsProfile.Low, DeviceGraphics.profile(isLowRamDevice = true, memoryClassMb = 384))
        assertEquals(GraphicsProfile.Low, DeviceGraphics.profile(isLowRamDevice = false, memoryClassMb = 96))
    }

    @Test
    fun `mid-range heap downscales instead of skipping`() {
        assertEquals(GraphicsProfile.Medium, DeviceGraphics.profile(isLowRamDevice = false, memoryClassMb = 160))
    }

    @Test
    fun `higher heap keeps the full 3D path`() {
        assertEquals(GraphicsProfile.High, DeviceGraphics.profile(isLowRamDevice = false, memoryClassMb = 256))
        assertEquals(GraphicsProfile.High, DeviceGraphics.profile(isLowRamDevice = false, memoryClassMb = 512))
    }

    @Test
    fun `thresholds are documented constants`() {
        assertTrue(DeviceGraphics.LOW_MEMORY_CLASS_MB < DeviceGraphics.MEDIUM_MEMORY_CLASS_MB)
        assertFalse(DeviceGraphics.GLB_ASSET_PATH.isBlank())
    }
}
