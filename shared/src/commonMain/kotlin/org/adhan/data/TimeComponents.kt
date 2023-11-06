package org.adhan.data

import kotlinx.datetime.*

data class TimeComponents(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    companion object {
        fun fromDouble(value: Double): TimeComponents? {
            if (value.isInfinite() || value.isNaN()) {
                return null
            }

            val hours = kotlin.math.floor(value).toInt()
            val minutes = kotlin.math.floor((value - hours) * 60.0).toInt()
            val seconds = kotlin.math.floor((value - hours - minutes / 60.0) * 3600.0).toInt()
            return TimeComponents(hours, minutes, seconds)
        }
    }

    fun dateComponents(date: DateComponents): Instant {
        val dateTime = LocalDateTime(date.year, date.month, date.day, hours, minutes, seconds)
        // Conversion to Instant requires a TimeZone
        return dateTime.toInstant(TimeZone.UTC)
    }
}
