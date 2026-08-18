package com.mrbitches.doomsy.util

import android.app.ActivityManager
import android.content.Context

enum class GraphicsProfile {
    /** Skip the GLB and render a 2D mask instead. */
    Low,

    /** Load the GLB at a reduced scale without clip animation. */
    Medium,

    /** Full-quality SceneView path. */
    High,
}

object DeviceGraphics {

    const val GLB_ASSET_PATH = "models/doomsy.glb"
    const val LOW_MEMORY_CLASS_MB = 128
    const val MEDIUM_MEMORY_CLASS_MB = 192

    fun profile(isLowRamDevice: Boolean, memoryClassMb: Int): GraphicsProfile {
        if (isLowRamDevice || memoryClassMb < LOW_MEMORY_CLASS_MB) {
            return GraphicsProfile.Low
        }
        if (memoryClassMb < MEDIUM_MEMORY_CLASS_MB) {
            return GraphicsProfile.Medium
        }
        return GraphicsProfile.High
    }

    fun profile(context: Context): GraphicsProfile {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return profile(manager.isLowRamDevice, manager.memoryClass)
    }

    fun hasGlbAsset(context: Context, path: String = GLB_ASSET_PATH): Boolean {
        return try {
            context.assets.open(path).close()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun shouldLoadGlb(context: Context): Boolean {
        return hasGlbAsset(context) && profile(context) != GraphicsProfile.Low
    }
}
