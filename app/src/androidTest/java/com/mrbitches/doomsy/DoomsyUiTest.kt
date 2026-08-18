package com.mrbitches.doomsy

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mrbitches.doomsy.data.DoomTracks
import com.mrbitches.doomsy.data.DoomsySessionStore
import com.mrbitches.doomsy.util.DeviceGraphics
import com.mrbitches.doomsy.util.GraphicsProfile
import com.mrbitches.doomsy.util.SpotifyIntent
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.junit.runner.RunWith

class ClearDoomsySessionRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                context.getSharedPreferences(DoomsySessionStore.PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit()
                DeviceGraphics.profileOverride = GraphicsProfile.Low
                try {
                    base.evaluate()
                } finally {
                    DeviceGraphics.profileOverride = null
                }
            }
        }
    }
}

@RunWith(AndroidJUnit4::class)
class DoomsyUiTest {

    @get:Rule(order = 0)
    val clearSession = ClearDoomsySessionRule()

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun introSkipReachesComposer() {
        composeRule.onNodeWithContentDescription("Doomsy intro. Double tap to skip.").performClick()
        composeRule.waitUntil(timeoutMillis = 8_000) {
            composeRule.onAllNodes(hasContentDescription("Message Doomsy"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Message Doomsy").assertIsDisplayed()
    }

    @Test
    fun composerSendShowsReply() {
        skipIntro()

        composeRule.onNodeWithContentDescription("Message Doomsy").performClick()
        composeRule.onNodeWithContentDescription("Message Doomsy").performTextInput("hello")
        composeRule.waitUntil(timeoutMillis = 8_000) {
            runCatching {
                composeRule.onNodeWithContentDescription("Send message").assertHasClickAction()
            }.isSuccess
        }
        composeRule.onNodeWithContentDescription("Send message").performClick()

        composeRule.waitUntil(timeoutMillis = 8_000) {
            composeRule.onAllNodes(hasContentDescription("Your message"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithText("hello").assertIsDisplayed()
        // Cloud SSE or local fallback both append a second assistant bubble.
        composeRule.waitUntil(timeoutMillis = 45_000) {
            composeRule.onAllNodes(hasContentDescription("Doomsy message"))
                .fetchSemanticsNodes()
                .size >= 2
        }
    }

    @Test
    fun spotifyUrisAreValidOnEmulatorAndTrackCardsRender() {
        skipIntro()

        DoomTracks.allTracks.forEach { track ->
            val uri = SpotifyIntent.buildUri(track.spotifyUri)
            assertTrue(
                "Bad Spotify URI for ${track.name}: $uri",
                uri.startsWith("https://open.spotify.com/track/"),
            )
            val id = uri.removePrefix("https://open.spotify.com/track/")
            assertTrue("Missing track id for ${track.name}", id.isNotBlank())
        }

        composeRule.onNodeWithContentDescription("Show shuffled tracks").performClick()
        composeRule.waitUntil(timeoutMillis = 8_000) {
            composeRule.onAllNodes(hasContentDescription("on Spotify", substring = true))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onAllNodes(hasContentDescription("on Spotify", substring = true))
            .onFirst()
            .assertIsDisplayed()
    }

    private fun skipIntro() {
        composeRule.onNodeWithContentDescription("Doomsy intro. Double tap to skip.").performClick()
        composeRule.waitUntil(timeoutMillis = 8_000) {
            composeRule.onAllNodes(hasContentDescription("Message Doomsy"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
