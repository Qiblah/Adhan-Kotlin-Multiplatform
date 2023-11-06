package org.adhan.data

import kotlinx.datetime.*
import kotlin.math.floor

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

            val hours = floor(value).toInt()
            val minutes = floor((value - hours) * 60.0).toInt()
            val seconds = floor((value - (hours + minutes / 60.0)) * 60 * 60).toInt()
            return TimeComponents(hours, minutes, seconds)
        }
    }

    fun dateComponents(date: DateComponents): Instant {
        val dateTime = LocalDateTime(
            year = date.year,
            monthNumber = date.month,
            dayOfMonth = date.day,
            hour = hours,
            minute = minutes,
            second = seconds,
        )
        // Conversion to Instant requires a TimeZone
        return dateTime.toInstant(TimeZone.UTC)
    }
}
