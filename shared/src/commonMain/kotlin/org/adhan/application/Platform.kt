package org.adhan.application

import kotlinx.datetime.*

expect val platform: String

class Greeting {
    val timeZone : TimeZone.Companion = TimeZone

    fun greeting() = "Hello, $platform!"

    fun myTimeZone() = timeZone.availableZoneIds

}
