package org.adhan.internal

import org.adhan.Coordinates
import org.adhan.internal.DoubleUtil.toDegrees
import org.adhan.internal.DoubleUtil.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class QiblaUtil {
    companion object {
        private val Makkah = Coordinates(21.4225241, 39.8261818)

        fun calculateQiblaDirection(coordinates: Coordinates): Double {
            // Equation from "Spherical Trigonometry For the use of colleges and schools" page 50
            val longitudeDelta =
                toRadians(Makkah.longitude) - toRadians(coordinates.longitude)
            val latitudeRadians = toRadians(coordinates.latitude)
            val term1 = sin(longitudeDelta)
            val term2 = cos(latitudeRadians) * tan(toRadians(Makkah.latitude))
            val term3 = sin(latitudeRadians) * cos(longitudeDelta)

            val angle = atan2(term1, term2 - term3)
            return DoubleUtil.unwindAngle(toDegrees(angle))
        }
    }
}
