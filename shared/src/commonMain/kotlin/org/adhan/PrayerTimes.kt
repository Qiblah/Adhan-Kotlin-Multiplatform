package org.adhan

import kotlinx.datetime.*
import org.adhan.data.CalendarUtil.isLeapYear
import org.adhan.data.DateComponents
import kotlin.math.abs
import kotlin.math.round

class PrayerTimes(
    val coordinates: Coordinates,
    val dateComponents: DateComponents,
    val calculationParameters: CalculationParameters,
) {
    val fajr: LocalDateTime? = null

    val sunrise: LocalDateTime? = null

    val dhuhr: LocalDateTime? = null

    val asr: LocalDateTime? = null

    val maghrib: LocalDateTime? = null

    val isha: LocalDateTime? = null

    fun currentPrayer(): Prayer {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return when {
            now >= isha!! -> Prayer.Isha
            now >= maghrib!! -> Prayer.Maghrib
            now >= asr!! -> Prayer.Asr
            now >= dhuhr!! -> Prayer.Dhuhr
            now >= sunrise!! -> Prayer.Sunrise
            now >= fajr!! -> Prayer.Fajr
            else -> Prayer.None
        }
    }

    fun nextPrayer(time: LocalDateTime): Prayer {
        return when {
            time < fajr!! -> Prayer.Fajr
            time < sunrise!! -> Prayer.Sunrise
            time < dhuhr!! -> Prayer.Dhuhr
            time < asr!! -> Prayer.Asr
            time < maghrib!! -> Prayer.Maghrib
            time < isha!! -> Prayer.Isha
            else -> Prayer.None
        }
    }

    fun timeForPrayer(prayer: Prayer):  LocalDateTime? {
        return when (prayer) {
            Prayer.Fajr -> fajr
            Prayer.Sunrise -> sunrise
            Prayer.Dhuhr -> dhuhr
            Prayer.Asr -> asr
            Prayer.Maghrib -> maghrib
            Prayer.Isha -> isha
            Prayer.None -> null
        }
    }
    fun seasonAdjustedMorningTwilight(
        latitude: Double,
        dayOfYear: Int,
        year: Int,
        sunrise: LocalDateTime,
        timeZone: TimeZone,
    ): LocalDateTime {
        val a = 75 + ((28.65 / 55.0) * abs(latitude))
        val b = 75 + ((19.44 / 55.0) * abs(latitude))
        val c = 75 + ((32.74 / 55.0) * abs(latitude))
        val d = 75 + ((48.10 / 55.0) * abs(latitude))

        val dyy = daysSinceSolstice(dayOfYear, year, latitude)

        val adjustment = when {
            dyy < 91 -> a + (b - a) / 91.0 * dyy
            dyy < 137 -> b + (c - b) / 46.0 * (dyy - 91)
            dyy < 183 -> c + (d - c) / 46.0 * (dyy - 137)
            dyy < 229 -> d + (c - d) / 46.0 * (dyy - 183)
            dyy < 275 -> c + (b - c) / 46.0 * (dyy - 229)
            else -> b + (a - b) / 91.0 * (dyy - 275)
        }

        val adjustmentInSeconds = round(adjustment * 60.0).toInt()

        // Convert LocalDateTime to Instant for subtracting seconds accurately
        val sunriseInstant = sunrise.toInstant(timeZone)
        val adjustedInstant = sunriseInstant.minus(adjustmentInSeconds, DateTimeUnit.SECOND, timeZone)

        // Convert back to LocalDateTime
        return adjustedInstant.toLocalDateTime(timeZone)
    }

    fun seasonAdjustedEveningTwilight(
        latitude: Double,
        dayOfYear: Int,
        year: Int,
        sunset: LocalDateTime,
        timeZone: TimeZone,
    ): LocalDateTime {
        val a = 75 + ((25.60 / 55.0) * abs(latitude))
        val b = 75 + ((2.050 / 55.0) * abs(latitude))
        val c = 75 - ((9.210 / 55.0) * abs(latitude))
        val d = 75 + ((6.140 / 55.0) * abs(latitude))

        val dyy = daysSinceSolstice(dayOfYear, year, latitude)

        val adjustment = when {
            dyy < 91 -> a + (b - a) / 91.0 * dyy
            dyy < 137 -> b + (c - b) / 46.0 * (dyy - 91)
            dyy < 183 -> c + (d - c) / 46.0 * (dyy - 137)
            dyy < 229 -> d + (c - d) / 46.0 * (dyy - 183)
            dyy < 275 -> c + (b - c) / 46.0 * (dyy - 229)
            else -> b + (a - b) / 91.0 * (dyy - 275)
        }

        val adjustmentInSeconds = round(adjustment * 60.0).toInt()

        // Convert LocalDateTime to Instant for adding seconds accurately
        val sunsetInstant = sunset.toInstant(timeZone = timeZone)
        val adjustedInstant = sunsetInstant.plus(adjustmentInSeconds, DateTimeUnit.SECOND, timeZone)

        // Convert back to LocalDateTime
        return adjustedInstant.toLocalDateTime(timeZone)
    }
    private fun daysSinceSolstice(dayOfYear: Int, year: Int, latitude: Double): Int {
        val northernOffset = 10
        val isLeapYear = isLeapYear(year)
        val southernOffset = if (isLeapYear) 173 else 172
        val daysInYear = if (isLeapYear) 366 else 365

        return if (latitude >= 0) {
            (dayOfYear + northernOffset).let {
                if (it >= daysInYear) it - daysInYear else it
            }
        } else {
            (dayOfYear - southernOffset).let {
                if (it < 0) it + daysInYear else it
            }
        }
    }

}