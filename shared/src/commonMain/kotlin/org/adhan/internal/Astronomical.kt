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
        /* Equation from Astronomical Algorithms page 163 */
        val term1 = 280.4664567
        val term2 = 36000.76983 * T
        val term3 = 0.0003032 * T.pow(2)
        val L0 = term1 + term2 + term3
        return unwindAngle(L0)
    }

    private fun unwindAngle(angle: Double): Double {
        return angle % 360.0
    }

    /**
     * The geometric mean longitude of the moon in degrees
     * @param T the julian century
     * @return the geometric mean longitude of the moon in degrees
     */
    fun meanLunarLongitude(T: Double): Double {
        /* Equation from Astronomical Algorithms page 144 */
        val term1 = 218.3165
        val term2 = 481267.8813 * T
        val Lp = term1 + term2
        return unwindAngle(Lp)
    }

    fun apparentSolarLongitude(T: Double, L0: Double): Double {
        /* Equation from Astronomical Algorithms page 164 */
        val longitude = L0 + solarEquationOfTheCenter(T, meanSolarAnomaly(T))
        val omega = 125.04 - (1934.136 * T)
        val lambda = longitude - 0.00569 - (0.00478 * sin(omega * (PI / 180)))
        return unwindAngle(lambda)
    }

    fun meanSolarAnomaly(T: Double): Double {
        /* Equation from Astronomical Algorithms page 163 */
        val term1 = 357.52911
        val term2 = 35999.05029 * T
        val term3 = 0.0001537 * T.pow(2.0)
        val M = term1 + term2 - term3
        return unwindAngle(M)
    }

    fun ascendingLunarNodeLongitude(T: Double): Double {
        /* Equation from Astronomical Algorithms page 144 */
        val term1 = 125.04452
        val term2 = 1934.136261 * T
        val term3 = 0.0020708 * T.pow(2.0)
        val term4 = T.pow(3.0) / 450000
        val omega = term1 - term2 + term3 + term4
        return unwindAngle(omega)
    }

    fun meanObliquityOfTheEcliptic(T: Double): Double {
        /* Equation from Astronomical Algorithms page 147 */
        val term1 = 23.439291
        val term2 = 0.013004167 * T
        val term3 = 0.0000001639 * T.pow(2.0)
        val term4 = 0.0000005036 * T.pow(3.0)
        return term1 - term2 - term3 + term4
    }

    fun nutationInObliquity(T: Double, L0: Double, Lp: Double, Ω: Double): Double {
        /* Equation from Astronomical Algorithms page 144 */
        val term1 = (9.2 / 3600) * cos(Ω.toRadian())
        val term2 = (0.57 / 3600) * cos(2 * L0.toRadian())
        val term3 = (0.10 / 3600) * cos(2 * Lp.toRadian())
        val term4 = (0.09 / 3600) * cos(2 * Ω.toRadian())
        return term1 + term2 + term3 - term4
    }

    fun altitudeOfCelestialBody(φ: Double, δ: Double, H: Double): Double {
        /* Equation from Astronomical Algorithms page 93 */
        val term1 = sin(φ.toRadian()) * sin(δ.toRadian())
        val term2 = cos(φ.toRadian()) * cos(δ.toRadian()) * cos(H.toRadian())
        return asin(term1 + term2).toDegrees()
    }




    /**
     * The Sun's equation of the center in degrees.
     * @param T the julian century
     * @param M the mean anomaly
     * @return the sun's equation of the center in degrees
     */
    fun solarEquationOfTheCenter(T: Double, M: Double): Double {
        val Mrad = M.toRadian()
        val term1 = (1.914602 - (0.004817 * T) - (0.000014 * T.pow(2))) * sin(Mrad)
        val term2 = (0.019993 - (0.000101 * T)) * sin(2 * Mrad)
        val term3 = 0.000289 * sin(3 * Mrad)
        return term1 + term2 + term3
    }


}

fun Double.toRadian(): Double = this / 180 * PI
fun Double.toDegree(): Double = this * 180 / PI