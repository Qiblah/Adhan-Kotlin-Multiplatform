package org.adhan

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SunnahTimes(prayerTimes: PrayerTimes) {
    val middleOfTheNight: Instant
    val lastThirdOfTheNight: Instant

    init {
        val timezone = TimeZone.currentSystemDefault()

        // Convert PrayerTimes to LocalDateTime
        val maghribLocal = Instant.fromEpochMilliseconds(prayerTimes .time).toLocalDateTime(timezone)
        val fajrLocal = Instant.fromEpochMilliseconds(prayerTimes.fajr.time).toLocalDateTime(timezone)
        val tomorrowFajrLocal = fajrLocal.plusDays(1)

        val nightDuration = tomorrowFajrLocal - maghribLocal

        // Calculate middle of the night
        middleOfTheNight = maghribLocal.plusSeconds((nightDuration.inWholeSeconds / 2)).toInstant(timezone)

        // Calculate last third of the night
        lastThirdOfTheNight = maghribLocal.plusSeconds((nightDuration.inWholeSeconds * 2 / 3)).toInstant(timezone)
    }
}
