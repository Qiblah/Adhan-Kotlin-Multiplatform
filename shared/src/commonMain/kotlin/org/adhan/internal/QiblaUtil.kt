package org.adhan.internal

class QiblaUtil {
    companion object {
        private val MAKKAH = Coordinates(21.4225241, 39.8261818)

        fun calculateQiblaDirection(coordinates: Coordinates): Double {
            // Equation from "Spherical Trigonometry For the use of colleges and schools" page 50
            val longitudeDelta = Math.toRadians(MAKKAH.longitude) - Math.toRadians(coordinates.longitude)
            val latitudeRadians = Math.toRadians(coordinates.latitude)
            val term1 = kotlin.math.sin(longitudeDelta)
            val term2 = kotlin.math.cos(latitudeRadians) * kotlin.math.tan(Math.toRadians(MAKKAH.latitude))
            val term3 = kotlin.math.sin(latitudeRadians) * kotlin.math.cos(longitudeDelta)

            val angle = kotlin.math.atan2(term1, term2 - term3)
            return DoubleUtil.unwindAngle(kotlin.math.degrees(angle))
        }
    }
}

data class Coordinates(val latitude: Double, val longitude: Double)

fun Double.toDegrees(): Double = this * (180.0 / kotlin.math.PI)
