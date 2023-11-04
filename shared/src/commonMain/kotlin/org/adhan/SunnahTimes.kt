package org.adhan

import kotlinx.datetime.LocalDateTime
import org.adhan.data.CalendarUtil
import org.adhan.data.DateComponents
import kotlin.time.Duration

class SunnahTimes(prayerTimes: PrayerTimes) {
    private val middleOfTheNight: LocalDateTime
    private val lastThirdOfTheNight: LocalDateTime

    init {
        val currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents)
        val tomorrowPrayerTimesDate = CalendarUtil.addDays(currentPrayerTimesDate, 1)
        val tomorrowPrayerTimes = PrayerTimes(
            coordinates = prayerTimes.coordinates,
            dateComponents=DateComponents.fromUTC(tomorrowPrayerTimesDate),
            calculationParameters =prayerTimes.calculationParameters
        )

        val nightDuration = Duration.seconds(
            (tomorrowPrayerTimes.fajr.epochSeconds - prayerTimes.maghrib.epochSeconds)
        )
        middleOfTheNight = prayerTimes.maghrib.plus(nightDuration.dividedBy(2)).roundedMinute()
        lastThirdOfTheNight = prayerTimes.maghrib.plus(nightDuration.multipliedBy(2.0 / 3.0)).roundedMinute()
    }
}