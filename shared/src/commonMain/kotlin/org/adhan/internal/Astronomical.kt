package org.adhan.internal

import kotlin.math.*

/**
 * Astronomical equations
 */
object Astronomical {

    /**
     * The geometric mean longitude of the sun in degrees.
     * @param T the julian century
     * @return the geometric longitude of the sun in degrees
     */
    fun meanSolarLongitude(T: Double): Double {
        val term1 = 280.4664567
        val term2 = 36000.76983 * T
        val term3 = 0.0003032 * T.pow(2)
        val L0 = term1 + term2 + term3
        return unwindAngle(L0)
    }

    // Other methods would be similarly converted to Kotlin.
    // For instance, here's another method:

    /**
     * The Sun's equation of the center in degrees.
     * @param T the julian century
     * @param M the mean anomaly
     * @return the sun's equation of the center in degrees
     */
    fun solarEquationOfTheCenter(T: Double, M: Double): Double {
        val Mrad = Math.toRadians(M)
        val term1 = (1.914602 - (0.004817 * T) - (0.000014 * T.pow(2))) * sin(Mrad)
        val term2 = (0.019993 - (0.000101 * T)) * sin(2 * Mrad)
        val term3 = 0.000289 * sin(3 * Mrad)
        return term1 + term2 + term3
    }

    // Utility method to unwind an angle
    fun unwindAngle(angle: Double): Double {
        return ((angle % 360) + 360) % 360
    }

    // ...additional methods and conversions would follow...

}
