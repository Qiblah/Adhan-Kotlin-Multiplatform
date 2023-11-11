package org.adhan

import kotlinx.datetime.*
import org.zephyr.app.adhan.PrayerTimes
import kotlin.math.abs

class SunnahTimes(prayerTimes: PrayerTimes) {

    /* The midpoint between Maghrib and Fajr */
    private val middleOfTheNight: LocalDateTime

    /* The beginning of the last third of the period between Maghrib and Fajr,
         a recommended time to perform Qiyam */
    private val lastThirdOfTheNight: LocalDateTime

    init {
        val timezone = TimeZone.currentSystemDefault()

        val maghribInstant = prayerTimes.maghrib!!.toInstant(timezone)
        val nextDayFajr = LocalDateTime(
            year = prayerTimes.dateComponents.year,
            monthNumber = prayerTimes.dateComponents.month,
            dayOfMonth = prayerTimes.dateComponents.day + 1,
            hour = prayerTimes.fajr!!.hour,
            minute = prayerTimes.fajr!!.minute,
            second = prayerTimes.fajr!!.second
        )
        val fajrInstant = nextDayFajr.toInstant(timezone)

        val nightDurationInSeconds = abs(fajrInstant.epochSeconds - maghribInstant.epochSeconds)

        middleOfTheNight = maghribInstant.plus(nightDurationInSeconds / 2, DateTimeUnit.SECOND, timezone).toLocalDateTime(timezone)
        lastThirdOfTheNight = maghribInstant.plus(2 * nightDurationInSeconds / 3, DateTimeUnit.SECOND, timezone).toLocalDateTime(timezone)
    }
}
