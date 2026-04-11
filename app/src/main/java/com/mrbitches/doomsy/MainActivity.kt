package com.mrbitches.doomsy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.mrbitches.doomsy.ui.intro.IntroScreen
import com.mrbitches.doomsy.ui.main.MainScreen
import com.mrbitches.doomsy.ui.theme.DoomsyTheme

class MainActivity : ComponentActivity() {

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            DoomsyTheme {
                var introComplete by remember { mutableStateOf(false) }

                if (!introComplete) {
                    IntroScreen(onIntroComplete = { introComplete = true })
                } else {
                    MainScreen()
                }
            }
        }
    }
}
