package org.adhan.internal

import kotlinx.datetime.*

object CalendricalHelper {

    /**
     * The Julian Day for a given Gregorian date
     * @param year the year
     * @param month the month
     * @param day the day
     * @return the julian day
     */
    fun julianDay(year: Int, month: Int, day: Int): Double {
        return julianDay(year, month, day, 0.0)
    }

    /**
     * The Julian Day for a given date
     * @param instant the date
     * @return the julian day
     */
    fun julianDay(instant: Instant): Double {
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)
        return julianDay(
            year = dateTime.year,
            month = dateTime.monthNumber,
            day = dateTime.dayOfMonth,
            hours = dateTime.hour + dateTime.minute / 60.0
        )
    }

    /**
     * The Julian Day for a given Gregorian date
     * @param year the year
     * @param month the month
     * @param day the day
     * @param hours hours
     * @return the julian day
     */
    private fun julianDay(year: Int, month: Int, day: Int, hours: Double): Double {
        // Equation from Astronomical Algorithms page 60
        val y = if (month > 2) year else year - 1
        val m = if (month > 2) month else month + 12
        val d = day + hours / 24.0

        val a = y / 100
        val b = 2 - a + a / 4

        val i0 = (365.25 * (y + 4716)).toInt()
        val i1 = (30.6001 * (m + 1)).toInt()
        return i0 + i1 + d + b - 1524.5
    }

    /**
     * Julian century from the epoch.
     * @param jd the julian day
     * @return the julian century from the epoch
     */
    fun julianCentury(jd: Double): Double {
        // Equation from Astronomical Algorithms page 163
        return (jd - 2451545.0) / 36525
    }
}
