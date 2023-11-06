package org.adhan.application

import kotlinx.datetime.*
import org.adhan.CalculationMethod
import org.adhan.Coordinates
import org.adhan.PrayerTimes
import org.adhan.data.DateComponents
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect val platform: String

class Greeting {
    fun greeting() {
        mainn()
        "Hello, $platform!"
    }
}

fun mainn() {
    val coordinates = Coordinates(35.760350, 10.822078)
    val now = Clock.System.now()
    val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateComponents = DateComponents(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth)
    val parameters = CalculationMethod.MuslimWorldLeague.getParameters()

    val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)

    // Formatter in kotlinx-datetime
    // You need to write your own function to format LocalDateTime to the desired string format

    println("Fajr: ${prayerTimes.fajr?.formatTime()}")
    println("Sunrise: ${prayerTimes.sunrise?.formatTime()}")
    println("Dhuhr: ${prayerTimes.dhuhr?.formatTime()}")
    println("Asr: ${prayerTimes.asr?.formatTime()}")
    println("Maghrib: ${prayerTimes.maghrib?.formatTime()}")
    println("Isha: ${prayerTimes.isha?.formatTime()}")
}

// This is a simple extension function to format LocalDateTime to a string with format "hh:mm a"
// You need to implement this based on how you want to display the time
fun LocalDateTime.formatTime(): String {
    // This is just a placeholder. You should implement the actual formatting logic
    // Example: "04:08 PM". You might need to use a library like java-time-formatter or write your own formatter.
    return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
}

// You should also update your Coordinates, DateComponents, CalculationMethod, and PrayerTimes classes to use kotlinx-datetime.

