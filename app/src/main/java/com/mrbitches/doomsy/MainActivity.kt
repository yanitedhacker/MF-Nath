package com.mrbitches.doomsy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mrbitches.doomsy.data.DoomsySessionStore
import com.mrbitches.doomsy.ui.intro.IntroScreen
import com.mrbitches.doomsy.ui.main.MainScreen
import com.mrbitches.doomsy.ui.theme.DoomsyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val sessionStore = DoomsySessionStore(this)

        setContent {
            DoomsyTheme {
                var introComplete by remember { mutableStateOf(sessionStore.isIntroSeen()) }

                Crossfade(
                    targetState = introComplete,
                    animationSpec = tween(420),
                    label = "introCrossfade",
                ) { isReady ->
                    if (!isReady) {
                        IntroScreen(
                            onIntroComplete = {
                                sessionStore.setIntroSeen()
                                introComplete = true
                            },
                        )
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }
}
