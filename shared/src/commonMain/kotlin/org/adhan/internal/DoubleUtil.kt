package org.adhan.internal

object DoubleUtil {

    fun normalizeWithBound(value: Double, max: Double): Double {
        return value - (max * kotlin.math.floor(value / max))
    }

    fun unwindAngle(value: Double): Double {
        return normalizeWithBound(value, 360.0)
    }

    fun closestAngle(angle: Double): Double {
        return if (angle in -180.0..180.0) {
            angle
        } else {
            angle - (360 * kotlin.math.round(angle / 360))
        }
    }
}
