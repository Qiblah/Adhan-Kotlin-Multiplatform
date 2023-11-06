package org.adhan

import kotlinx.datetime.*
import kotlin.math.abs

class SunnahTimes(prayerTimes: PrayerTimes) {

    /* The midpoint between Maghrib and Fajr */
    val middleOfTheNight: LocalDateTime

    /* The beginning of the last third of the period between Maghrib and Fajr,
         a recommended time to perform Qiyam */
    val lastThirdOfTheNight: LocalDateTime

    init {
        // Assuming we have a timezone. For accurate calculations, it should be the timezone relevant to the prayer times.
        val timezone = TimeZone.currentSystemDefault()

        // Convert maghrib and fajr to Instant for arithmetic operations
        val maghribInstant = prayerTimes.maghrib!!.toInstant(timezone)
        val nextDayFajr = LocalDateTime(
            year = prayerTimes.dateComponents.year,
            monthNumber = prayerTimes.dateComponents.month,
            dayOfMonth = prayerTimes.dateComponents.day + 1,
            hour = prayerTimes.fajr!!.hour,
            minute = prayerTimes.fajr.minute,
            second = prayerTimes.fajr.second
        )
        val fajrInstant = nextDayFajr.toInstant(timezone)

        val nightDurationInSeconds = abs(fajrInstant.epochSeconds - maghribInstant.epochSeconds)

        middleOfTheNight = maghribInstant.plus(nightDurationInSeconds / 2, DateTimeUnit.SECOND, timezone).toLocalDateTime(timezone)
        lastThirdOfTheNight = maghribInstant.plus(2 * nightDurationInSeconds / 3, DateTimeUnit.SECOND, timezone).toLocalDateTime(timezone)
    }
}
