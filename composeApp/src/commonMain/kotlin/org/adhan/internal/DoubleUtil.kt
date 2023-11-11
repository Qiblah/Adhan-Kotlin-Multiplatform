package org.adhan.internal

import kotlin.math.floor
import kotlin.math.round

object DoubleUtil {

    private fun normalizeWithBound(value: Double, max: Double): Double {
        return value - (max * floor(value / max))
    }

    fun unwindAngle(value: Double): Double {
        return normalizeWithBound(value, 360.0)
    }

    fun closestAngle(angle: Double): Double {
        return if (angle in -180.0..180.0) {
            angle
        } else {
            angle - (360 * round(angle / 360))
        }
    }
    private const val DEGREES_TO_RADIANS = 0.017453292519943295
    private const val RADIANS_TO_DEGREES = 57.29577951308232
    /**
     * Converts an angle measured in degrees to an approximately
     * equivalent angle measured in radians.  The conversion from
     * degrees to radians is generally inexact.
     *
     * @param   angDeg   an angle, in degrees
     * @return  the measurement of the angle `angDeg`
     * in radians.
     */
    fun toRadians(angDeg: Double): Double {
        return angDeg * DEGREES_TO_RADIANS
    }

    /**
     * Converts an angle measured in radians to an approximately
     * equivalent angle measured in degrees.  The conversion from
     * radians to degrees is generally inexact; users should
     * *not* expect `cos(toRadians(90.0))` to exactly
     * equal `0.0`.
     *
     * @param  angRad an angle, in radians
     * @return the measurement of the angle `angRad`
     * in degrees.
     */
    fun toDegrees(angRad: Double): Double {
        return angRad * RADIANS_TO_DEGREES
    }
}
