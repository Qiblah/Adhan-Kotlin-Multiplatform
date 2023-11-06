package org.adhan

import kotlinx.datetime.LocalDateTime
import org.adhan.data.CalendarUtil
import org.adhan.data.DateComponents
import kotlin.time.Duration.Companion.days

class SunnahTimes(prayerTimes: PrayerTimes) {
//    val middleOfTheNight: LocalDateTime
//    val lastThirdOfTheNight: LocalDateTime

    init {
        // Assuming CalendarUtil.resolveTime returns a LocalDateTime
        val currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents)

        // Add one day to get "tomorrow"
        val tomorrowPrayerTimesDate = currentPrayerTimesDate + 1.days

        // Assuming we can create a DateComponents instance from a LocalDateTime
        val tomorrowDateComponents = DateComponents.from(tomorrowPrayerTimesDate)

        // Create PrayerTimes for tomorrow
        val tomorrowPrayerTimes = PrayerTimes(
            prayerTimes.coordinates,
            tomorrowDateComponents,
            prayerTimes.calculationParameters
        )

        // Calculate the duration between Maghrib and Fajr in seconds
//        val nightDurationInSeconds = Duration.between(
//            prayerTimes.maghrib,
//            tomorrowPrayerTimes.fajr
//        ).inWholeSeconds

        // Calculate middle of the night and last third of the night
//        middleOfTheNight = prayerTimes.maghrib.plus((nightDurationInSeconds / 2).seconds)
//        lastThirdOfTheNight = prayerTimes.maghrib.plus((nightDurationInSeconds * 2 / 3).seconds)
        // If you have rounding to the nearest minute in CalendarUtil, apply it here:
        // middleOfTheNight = CalendarUtil.roundToNearestMinute(middleOfTheNight)
        // lastThirdOfTheNight = CalendarUtil.roundToNearestMinute(lastThirdOfTheNight)
    }
}