package org.adhan.application

import org.junit.Assert.assertTrue
import org.junit.Test

class PlatformTest {

    @Test
    fun testExample() {
        assertTrue("Check android is mentioned", Greeting().greeting().contains("android"))
    }
}