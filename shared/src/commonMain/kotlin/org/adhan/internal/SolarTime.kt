package org.adhan.internal

import org.adhan.Coordinates
import org.adhan.data.DateComponents
import org.adhan.internal.DoubleUtil.toDegrees
import org.adhan.internal.DoubleUtil.toRadians
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.tan

class SolarTime(
    today: DateComponents,
    coordinates: Coordinates
) {
    val transit: Double
    val sunrise: Double
    val sunset: Double

    private val observer: Coordinates
    private val solar: SolarCoordinates
    private val prevSolar: SolarCoordinates
    private val nextSolar: SolarCoordinates
    private val approximateTransit: Double

    init {
        val julianDate = CalendricalHelper.julianDay(today.year, today.month, today.day)

        prevSolar = SolarCoordinates(julianDate - 1)
        solar = SolarCoordinates(julianDate)
        nextSolar = SolarCoordinates(julianDate + 1)

        approximateTransit = Astronomical.approximateTransit(
            L = coordinates.longitude,
            Θ0 = solar.apparentSiderealTime,
            α2 = solar.rightAscension
        )

        val solarAltitude = -50.0 / 60.0

        observer = coordinates
        transit = Astronomical.correctedTransit(
            m0 = approximateTransit,
            L = coordinates.longitude,
            Θ0 = solar.apparentSiderealTime,
            α2 = solar.rightAscension,
            α1 = prevSolar.rightAscension,
            α3 = nextSolar.rightAscension
        )
        sunrise = Astronomical.correctedHourAngle(
            coordinates = coordinates,
            afterTransit = false,
            h0 = solarAltitude,
            m0 = approximateTransit,
            Θ0 = solar.apparentSiderealTime,
            α2 = solar.rightAscension,
            α1 = prevSolar.rightAscension,
            α3 = nextSolar.rightAscension,
            δ2 = solar.declination,
            δ1 = prevSolar.declination,
            δ3 = nextSolar.declination
        )
        sunset = Astronomical.correctedHourAngle(
            coordinates = coordinates,
            afterTransit = true,
            h0 = solarAltitude,
            m0 = approximateTransit,
            Θ0 = solar.apparentSiderealTime,
            α2 = solar.rightAscension,
            α1 = prevSolar.rightAscension,
            α3 = nextSolar.rightAscension,
            δ2 = solar.declination,
            δ1 = prevSolar.declination,
            δ3 = nextSolar.declination
        )
    }
    private fun hourAngle(angle: Double, afterTransit: Boolean): Double {
        return Astronomical.correctedHourAngle(
            h0 = angle,
            m0 = approximateTransit,
            coordinates = observer,
            afterTransit = afterTransit,
            Θ0 = solar.apparentSiderealTime,
            α2 = solar.rightAscension,
            α1 = prevSolar.rightAscension,
            α3 = nextSolar.rightAscension,
            δ2 = solar.declination,
            δ1 = prevSolar.declination,
            δ3 = nextSolar.declination
        )
    }

    // hours from transit
    fun afternoon(shadowLength: ShadowLength): Double {
        val tangent = abs(observer.latitude - solar.declination)
        val inverse = shadowLength.shadowLength + tan(toRadians(tangent))
        val angle = toDegrees(atan(1.0 / inverse))

        return hourAngle(angle, true)
    }
}