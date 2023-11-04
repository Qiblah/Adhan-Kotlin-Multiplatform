package org.adhan

class Qibla(coordinates: Coordinates) {
    companion object {
        private val MAKKAH = Coordinates(21.4225241, 39.8261818)
    }

    val direction: Double = QiblaUtil.calculateQiblaDirection(coordinates)
}