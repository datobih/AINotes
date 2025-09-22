package com.example.ainotes

import org.junit.Assert.assertEquals
import org.junit.Test

class SampleSanityTest {
    @Test
    fun `string template produces expected greeting`() {
        val name = "Android"
        val text = "Hello $name!"
        assertEquals("Hello Android!", text)
    }
}