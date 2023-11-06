package org.adhan

import kotlinx.datetime.*
import org.adhan.data.CalendarUtil.isLeapYear
import org.adhan.data.CalendarUtil.resolveTime
import org.adhan.data.CalendarUtil.roundedMinute
import org.adhan.data.DateComponents
import org.adhan.data.TimeComponents
import org.adhan.internal.SolarTime
import kotlin.math.*
import kotlin.time.Duration

class PrayerTimes(
    coordinates: Coordinates,
    dateComponents: DateComponents,
    calculationParameters: CalculationParameters,
) {
    var fajr: LocalDateTime? = null

    var sunrise: LocalDateTime? = null

    var dhuhr: LocalDateTime? = null

    var asr: LocalDateTime? = null

    var maghrib: LocalDateTime? = null

    var isha: LocalDateTime? = null

    init {
        var tempFajr: Instant? = null
        var tempSunrise: Instant? = null
        var tempDhuhr: Instant? = null
        var tempAsr: Instant? = null
        var tempMaghrib: Instant? = null
        var tempIsha: Instant? = null

        val prayerDate: Instant = resolveTime(dateComponents)
        val dayOfYear = prayerDate.toLocalDateTime(TimeZone.UTC).dayOfYear

        val tomorrowDate = prayerDate.plus(1, DateTimeUnit.DAY, TimeZone.UTC)

        val tomorrow = DateComponents.fromUTC(tomorrowDate)

        val solarTime = SolarTime(dateComponents, coordinates)

        val transitTimeComponents = TimeComponents.fromDouble(solarTime.transit)
        val transit = transitTimeComponents?.dateComponents(dateComponents)

        val sunriseTimeComponents = TimeComponents.fromDouble(solarTime.sunrise)
        val sunriseComponents = sunriseTimeComponents?.dateComponents(dateComponents)

        val sunsetTimeComponents = TimeComponents.fromDouble(solarTime.sunset)
        val sunsetComponents = sunsetTimeComponents?.dateComponents(dateComponents)

        val tomorrowSolarTime = SolarTime(tomorrow, coordinates)
        val tomorrowSunriseComponents = TimeComponents.fromDouble(tomorrowSolarTime.sunrise)

        val error = listOf(transit, sunriseComponents, sunsetComponents, tomorrowSunriseComponents).any { it == null }
        if (!error) {
            tempDhuhr = transit
            tempSunrise = sunriseComponents
            tempMaghrib = sunsetComponents

            val asrTimeComponents = TimeComponents.fromDouble(solarTime.afternoon(calculationParameters.madhab.getShadowLength()))
            asrTimeComponents?.let {
                tempAsr = it.dateComponents(dateComponents)
            }

            // Get night length
            val tomorrowSunrise = tomorrowSunriseComponents!!.dateComponents(tomorrow)
            val night = tomorrowSunrise.epochSeconds - sunsetComponents!!.epochSeconds

            val fajrTimeComponents = TimeComponents.fromDouble(solarTime.hourAngle(-calculationParameters.fajrAngle, false))
            fajrTimeComponents?.let {
                tempFajr = it.dateComponents(dateComponents)
            }

            if (calculationParameters.method == CalculationMethod.MoonSightingCommittee && coordinates.latitude >= 55) {
                tempFajr = sunriseComponents
            }

            val nightPortions = calculationParameters.nightPortions()
            val safeFajr: LocalDateTime
            if (calculationParameters.method == CalculationMethod.MoonSightingCommittee) {
                safeFajr = seasonAdjustedMorningTwilight(
                    coordinates.latitude,
                    dayOfYear,
                    dateComponents.year,
                    sunriseComponents?.toLocalDateTime(TimeZone.currentSystemDefault())!!,
                    timeZone = TimeZone.currentSystemDefault()
                )
            } else {
                val portion = nightPortions.fajr
                val nightFraction = night.multipliedBy(portion).dividedBy(1000)
                safeFajr = sunriseComponents.minus(nightFraction)
            }

            if (tempFajr == null || tempFajr < safeFajr) {
                tempFajr = safeFajr
            }

            // Isha calculation with check against safe value
            if (calculationParameters.ishaInterval > 0) {
                tempIsha = tempMaghrib.plus(calculationParameters.ishaInterval.toLong() * 60)
            } else {
                val ishaTimeComponents = TimeComponents.fromDouble(solarTime.hourAngle(-calculationParameters.ishaAngle, true))
                ishaTimeComponents?.let {
                    tempIsha = it.dateComponents(dateComponents)
                }

                if (calculationParameters.method == CalculationMethod.MoonSightingCommittee && coordinates.latitude >= 55) {
                    val nightFraction = night.dividedBy(7000)
                    tempIsha = sunsetComponents.plus(nightFraction)
                }

                val safeIsha: LocalDateTime
                if (calculationParameters.method == CalculationMethod.MoonSightingCommittee) {
                    safeIsha = seasonAdjustedEveningTwilight(coordinates.latitude, dayOfYear,
                        dateComponents.year, sunsetComponents?.toLocalDateTime(TimeZone.currentSystemDefault())!!
                        , sunsetComponents)
                } else {
                    val portion = nightPortions.isha
                    val nightFraction = night.multipliedBy(portion).dividedBy(1000)
                    safeIsha = sunsetComponents.plus(nightFraction)
                }

                if (tempIsha == null || tempIsha!! > safeIsha) {
                    tempIsha = safeIsha
                }
            }
        }

        if (error || tempAsr == null) {
            // if we don't have all prayer times then initialization failed
            fajr = null
            sunrise = null
            dhuhr = null
            asr = null
            maghrib = null
            isha = null
        } else {
            // Assign final times to public struct members with all offsets

        }
    }

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