package org.adhan.data

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.floor

data class TimeComponents(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    companion object {
        fun fromDouble(value: Double): TimeComponents? {
            if (value.isInfinite() || value.isNaN()) return null

            val hours = floor(value)
            val minutes = floor((value - hours) * 60.0)
            val seconds = floor((value - (hours + minutes / 60.0)) * 60 * 60)
            return TimeComponents(hours.toInt(), minutes.toInt(), seconds.toInt())
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
        return dateTime.toInstant(TimeZone.UTC)
    }
}
