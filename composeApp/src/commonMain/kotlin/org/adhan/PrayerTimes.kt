package org.zephyr.app.adhan

import kotlinx.datetime.*
import org.adhan.CalculationMethod
import org.adhan.CalculationParameters
import org.adhan.Coordinates
import org.adhan.Prayer
import org.adhan.data.CalendarUtil
import org.adhan.data.CalendarUtil.isLeapYear
import org.adhan.data.DateComponents
import org.adhan.data.DateComponents.Companion.minus
import org.adhan.data.DateComponents.Companion.plus
import org.adhan.data.TimeComponents
import org.adhan.internal.SolarTime
import kotlin.math.abs
import kotlin.math.round

class PrayerTimes(
    val dateComponents: DateComponents,
    private val coordinates: Coordinates,
    private val calculationParameters: CalculationParameters,
) {
    var fajr: LocalDateTime? = null
    var sunrise: LocalDateTime? = null
    var dhuhr: LocalDateTime? = null
    var asr: LocalDateTime? = null
    var maghrib: LocalDateTime? = null
    var isha: LocalDateTime? = null

    init {
        calculatePrayerTimes()
    }

    private fun calculatePrayerTimes() {
        val solarTime = SolarTime(dateComponents, coordinates)
        val tomorrowSolarTime = calculateTomorrowSolarTime()

        val tempFajr = calculateFajr(solarTime)
        val tempSunrise = calculateSunrise(solarTime)
        val tempDhuhr = calculateDhuhr(solarTime)
        val tempAsr = calculateAsr(solarTime)
        val tempMaghrib = calculateMaghrib(solarTime)
        val tempIsha = calculateIsha(solarTime, tomorrowSolarTime)

        if (listOf(tempSunrise, tempDhuhr, tempAsr, tempMaghrib, tempIsha).any { it == null }) {
            initializePrayerTimesToNull()
        } else {
            assignAndRoundPrayerTimes(tempFajr, tempSunrise, tempDhuhr, tempAsr, tempMaghrib, tempIsha)
        }
    }

    private fun calculateFajr(solarTime: SolarTime): LocalDateTime? {
        val timeComponents = TimeComponents.fromDouble(
            solarTime.hourAngle(-calculationParameters.fajrAngle, false)
        )
        var tempFajr = timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)

        if (calculationParameters.method == CalculationMethod.MoonSightingCommittee &&
            coordinates.latitude >= 55
        ) {
            val sunriseComponents = calculateSunrise(solarTime) ?: return null
            val night = calculateNightLength(sunriseComponents, calculateMaghrib(solarTime) ?: return null)
            tempFajr = sunriseComponents.minus(night / 7000, DateTimeUnit.SECOND, TimeZone.UTC)
        }

        val safeFajr = calculateSafeFajr(solarTime)
        return if (tempFajr == null || tempFajr < safeFajr!!) safeFajr else tempFajr
    }

    private fun calculateSunrise(solarTime: SolarTime): LocalDateTime? {
        val timeComponents = TimeComponents.fromDouble(solarTime.sunrise)
        return timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)
    }

    private fun calculateDhuhr(solarTime: SolarTime): LocalDateTime? {
        val timeComponents = TimeComponents.fromDouble(solarTime.transit)
        return timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)
    }

    private fun calculateAsr(solarTime: SolarTime): LocalDateTime? {
        val timeComponents =
            TimeComponents.fromDouble(solarTime.afternoon(calculationParameters.madhab.getShadowLength()))
        return timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)
    }

    private fun calculateMaghrib(solarTime: SolarTime): LocalDateTime? {
        val timeComponents = TimeComponents.fromDouble(solarTime.sunset)
        return timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)
    }

    private fun calculateIsha(solarTime: SolarTime, tomorrowSolarTime: SolarTime): LocalDateTime? {
        val tempIsha: LocalDateTime? = if (calculationParameters.ishaInterval > 0) {
            val tempMaghrib = calculateMaghrib(solarTime) ?: return null
            tempMaghrib.plus((calculationParameters.ishaInterval * 60).toLong(), DateTimeUnit.SECOND, TimeZone.UTC)
        } else {
            val timeComponents = TimeComponents.fromDouble(
                solarTime.hourAngle(-calculationParameters.ishaAngle, true)
            )
            timeComponents?.dateComponents(dateComponents)?.toLocalDateTime(TimeZone.UTC)
        }

        val safeIsha = calculateSafeIsha(solarTime, tomorrowSolarTime) ?: return null
        return if (tempIsha == null || tempIsha > safeIsha) safeIsha else tempIsha
    }

    private fun calculateSafeFajr(solarTime: SolarTime): LocalDateTime? {
        val sunrise = calculateSunrise(solarTime) ?: return null
        val sunset = calculateMaghrib(solarTime) ?: return null

        val nightDuration = calculateNightLength(sunrise, sunset)
        val portion = calculationParameters.nightPortions().fajr
        val nightFraction = (portion * nightDuration / 1000).toLong()

        return sunrise.minus(nightFraction, DateTimeUnit.SECOND, TimeZone.UTC)
    }

    private fun calculateSafeIsha(solarTime: SolarTime, tomorrowSolarTime: SolarTime): LocalDateTime? {
        val sunset = calculateMaghrib(solarTime) ?: return null
        val sunrise = calculateSunrise(tomorrowSolarTime) ?: return null

        val nightDuration = calculateNightLength(sunrise, sunset)
        val portion = calculationParameters.nightPortions().isha
        val nightFraction = (portion * nightDuration / 1000).toLong()

        return sunset.plus(nightFraction, DateTimeUnit.SECOND, TimeZone.UTC)
    }

    private fun assignAndRoundPrayerTimes(
        tempFajr: LocalDateTime?,
        tempSunrise: LocalDateTime?,
        tempDhuhr: LocalDateTime?,
        tempAsr: LocalDateTime?,
        tempMaghrib: LocalDateTime?,
        tempIsha: LocalDateTime?
    ) {
        fajr = roundedPrayerTime(
            tempFajr,
            calculationParameters.adjustments.fajr,
            calculationParameters.methodAdjustments.fajr
        )
        sunrise = roundedPrayerTime(
            tempSunrise,
            calculationParameters.adjustments.sunrise,
            calculationParameters.methodAdjustments.sunrise
        )
        dhuhr = roundedPrayerTime(
            tempDhuhr,
            calculationParameters.adjustments.dhuhr,
            calculationParameters.methodAdjustments.dhuhr
        )
        asr = roundedPrayerTime(
            tempAsr,
            calculationParameters.adjustments.asr,
            calculationParameters.methodAdjustments.asr
        )
        maghrib = roundedPrayerTime(
            tempMaghrib,
            calculationParameters.adjustments.maghrib,
            calculationParameters.methodAdjustments.maghrib
        )
        isha = roundedPrayerTime(
            tempIsha,
            calculationParameters.adjustments.isha,
            calculationParameters.methodAdjustments.isha
        )
    }

    private fun calculateTomorrowSolarTime(): SolarTime {
        val tomorrowDate = CalendarUtil.add(
            whenInstant = CalendarUtil.resolveTime(dateComponents),
            amount = 1,
            unit = DateTimeUnit.DAY
        )
        val tomorrowComponents = DateComponents.fromUTC(tomorrowDate)
        return SolarTime(tomorrowComponents, coordinates)
    }

    private fun roundedPrayerTime(
        prayerTime: LocalDateTime?,
        adjustment: Int,
        methodAdjustment: Int
    ): LocalDateTime? {
        if (prayerTime == null) return null

        var adjustedTime: Instant = CalendarUtil.add(
            prayerTime.toInstant(TimeZone.UTC),
            adjustment,
            unit = DateTimeUnit.MINUTE,
        )
        adjustedTime = CalendarUtil.add(adjustedTime, methodAdjustment, DateTimeUnit.MINUTE)
        return CalendarUtil.roundedMinute(adjustedTime).toLocalDateTime(TimeZone.UTC)
    }

    private fun calculateNightLength(sunrise: LocalDateTime, sunset: LocalDateTime): Long {
        return sunset.time.toNanosecondOfDay() - sunrise.time.toNanosecondOfDay()
    }

    private fun initializePrayerTimesToNull() {
        fajr = null
        sunrise = null
        dhuhr = null
        asr = null
        maghrib = null
        isha = null
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

    fun timeForPrayer(prayer: Prayer): LocalDateTime? {
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