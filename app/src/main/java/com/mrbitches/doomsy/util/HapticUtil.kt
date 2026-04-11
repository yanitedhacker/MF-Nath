package com.mrbitches.doomsy.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object Haptic {

    private const val TAG = "Haptic"

    private fun vibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get vibrator", e)
            null
        }
    }

    private fun safeVibrate(context: Context, block: (Vibrator) -> Unit) {
        try {
            val v = vibrator(context) ?: return
            if (!v.hasVibrator()) return
            block(v)
        } catch (e: Exception) {
            Log.w(TAG, "Vibration failed", e)
        }
    }

    fun introRumble(context: Context) {
        safeVibrate(context) { vibrator ->
            val effect = VibrationEffect.createWaveform(
                longArrayOf(0, 100, 50, 150, 50, 200, 100, 300),
                intArrayOf(0, 80, 0, 120, 0, 180, 0, 255),
                -1,
            )
            vibrator.vibrate(effect)
        }
    }

    fun tap(context: Context) {
        safeVibrate(context) { vibrator ->
            val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
    }

    fun press(context: Context) {
        safeVibrate(context) { vibrator ->
            val effect = VibrationEffect.createOneShot(50, 150)
            vibrator.vibrate(effect)
        }
    }
}
