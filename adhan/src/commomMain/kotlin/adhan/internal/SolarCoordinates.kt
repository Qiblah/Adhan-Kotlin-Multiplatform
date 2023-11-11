package org.adhan.internal

import org.adhan.internal.DoubleUtil.toDegrees
import org.adhan.internal.DoubleUtil.toRadians
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class SolarCoordinates(julianDay: Double) {

    /**
     * The declination of the sun, the angle between
     * the rays of the Sun and the plane of the Earth's
     * equator, in degrees.
     */
    val declination: Double

    /**
     *  Right ascension of the Sun, the angular distance on the
     * celestial equator from the vernal equinox to the hour circle,
     * in degrees.
     */
    val rightAscension: Double

    /**
     *  Apparent sidereal time, the hour angle of the vernal
     * equinox, in degrees.
     */
    val apparentSiderealTime: Double

    init {
        val T = CalendricalHelper.julianCentury(julianDay)
        val L0 = Astronomical.meanSolarLongitude(T)
        val Lp = Astronomical.meanLunarLongitude(T)
        val Ω = Astronomical.ascendingLunarNodeLongitude(T)
        val λ = toRadians(Astronomical.apparentSolarLongitude(T, L0))

        val θ0 = Astronomical.meanSiderealTime(T)
        val ΔΨ = Astronomical.nutationInLongitude(T, L0, Lp, Ω)
        val Δε = Astronomical.nutationInObliquity(T, L0, Lp, Ω)

        val ε0 = Astronomical.meanObliquityOfTheEcliptic(T)
        val εapp = toRadians(Astronomical.apparentObliquityOfTheEcliptic(T, ε0))

        // Equation from Astronomical Algorithms page 165
        declination = toDegrees(asin(sin(εapp) * sin(λ)))

        // Equation from Astronomical Algorithms page 165
        rightAscension = DoubleUtil.unwindAngle(
            toDegrees(atan2(cos(εapp) * sin(λ), cos(λ)))
        )

        // Equation from Astronomical Algorithms page 88
        apparentSiderealTime = θ0 + (((ΔΨ * 3600) * cos(toRadians(ε0 + Δε))) / 3600)
    }
}
