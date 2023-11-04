package org.adhan.internal

class SolarCoordinates(julianDay: Double) {

    val declination: Double
    val rightAscension: Double
    val apparentSiderealTime: Double

    init {
        val T = CalendricalHelper.julianCentury(julianDay)
        val L0 = Astronomical.meanSolarLongitude(T)
        val Lp = Astronomical.meanLunarLongitude(T)
        val Ω = Astronomical.ascendingLunarNodeLongitude(T)
        val λ = Math.toRadians(Astronomical.apparentSolarLongitude(T, L0))

        val θ0 = Astronomical.meanSiderealTime(T)
        val ΔΨ = Astronomical.nutationInLongitude(T, L0, Lp, Ω)
        val Δε = Astronomical.nutationInObliquity(T, L0, Lp, Ω)

        val ε0 = Astronomical.meanObliquityOfTheEcliptic(T)
        val εapp = Math.toRadians(Astronomical.apparentObliquityOfTheEcliptic(T, ε0))

        // Equation from Astronomical Algorithms page 165
        declination = Math.toDegrees(Math.asin(Math.sin(εapp) * Math.sin(λ)))

        // Equation from Astronomical Algorithms page 165
        rightAscension = DoubleUtil.unwindAngle(
            Math.toDegrees(Math.atan2(Math.cos(εapp) * Math.sin(λ), Math.cos(λ)))
        )

        // Equation from Astronomical Algorithms page 88
        apparentSiderealTime = θ0 + (((ΔΨ * 3600) * Math.cos(Math.toRadians(ε0 + Δε))) / 3600)
    }
}
