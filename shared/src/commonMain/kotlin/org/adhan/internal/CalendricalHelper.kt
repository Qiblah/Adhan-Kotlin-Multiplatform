package org.adhan.internal

import kotlinx.datetime.*

object CalendricalHelper {

    fun julianDay(year: Int, month: Int, day: Int): Double {
        return julianDay(year, month, day, 0.0)
    }

    fun julianDay(instant: Instant): Double {
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)
        return julianDay(
            dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth,
            dateTime.hour + dateTime.minute / 60.0
        )
    }

    private fun julianDay(year: Int, month: Int, day: Int, hours: Double): Double {
        // Equation from Astronomical Algorithms page 60
        val Y = if (month > 2) year else year - 1
        val M = if (month > 2) month else month + 12
        val D = day + hours / 24.0

        val A = Y / 100
        val B = 2 - A + A / 4

        val i0 = (365.25 * (Y + 4716)).toInt()
        val i1 = (30.6001 * (M + 1)).toInt()
        return i0 + i1 + D + B - 1524.5
    }

    fun julianCentury(JD: Double): Double {
        // Equation from Astronomical Algorithms page 163
        return (JD - 2451545.0) / 36525
    }
}
