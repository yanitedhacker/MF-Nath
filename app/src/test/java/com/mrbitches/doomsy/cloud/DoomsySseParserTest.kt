package com.mrbitches.doomsy.cloud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DoomsySseParserTest {

    @Test
    fun `workers AI response field is a token`() {
        assertEquals(
            "The mask",
            DoomsySseParser.tokenFromSseLine("""data: {"response":"The mask"}"""),
        )
    }

    @Test
    fun `openai-style delta content is a token`() {
        assertEquals(
            " turns.",
            DoomsySseParser.tokenFromSseLine(
                """data: {"choices":[{"delta":{"content":" turns."}}]}""",
            ),
        )
    }

    @Test
    fun `done events are ignored`() {
        assertNull(DoomsySseParser.tokenFromSseLine("data: [DONE]"))
        assertNull(DoomsySseParser.tokenFromSseLine("""data: {"done":true}"""))
        assertNull(DoomsySseParser.tokenFromSseLine("event: message"))
        assertNull(DoomsySseParser.tokenFromSseLine(""))
    }
}
