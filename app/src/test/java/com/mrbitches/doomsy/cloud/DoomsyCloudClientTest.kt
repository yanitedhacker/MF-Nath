package com.mrbitches.doomsy.cloud

import org.junit.Assert.assertEquals
import org.junit.Test

class DoomsyCloudClientTest {

    @Test
    fun `chatEndpoint appends chat when missing`() {
        assertEquals(
            "https://doomsy-chat.example.workers.dev/chat",
            DoomsyCloudClient.chatEndpoint("https://doomsy-chat.example.workers.dev"),
        )
    }

    @Test
    fun `chatEndpoint does not duplicate chat`() {
        assertEquals(
            "https://doomsy-chat.example.workers.dev/chat",
            DoomsyCloudClient.chatEndpoint("https://doomsy-chat.example.workers.dev/chat/"),
        )
    }

    @Test
    fun `healthEndpoint is derived from worker origin`() {
        assertEquals(
            "https://doomsy-chat.example.workers.dev/health",
            DoomsyCloudClient.healthEndpoint("https://doomsy-chat.example.workers.dev"),
        )
    }

    @Test
    fun `healthEndpoint strips a trailing chat path`() {
        assertEquals(
            "https://doomsy-chat.example.workers.dev/health",
            DoomsyCloudClient.healthEndpoint("https://doomsy-chat.example.workers.dev/chat"),
        )
    }
}
