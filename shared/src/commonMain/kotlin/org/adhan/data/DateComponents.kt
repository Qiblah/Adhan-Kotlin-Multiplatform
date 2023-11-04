package org.adhan.data

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DateComponents(val year: Int, val month: Int, val day: Int) {
    companion object {
        /**
         * Convenience method that returns a DateComponents from a given Instant
         */
        fun from(instant: Instant, timeZone: TimeZone = TimeZone.currentSystemDefault()): DateComponents {
            val dateTime = instant.toLocalDateTime(timeZone)
            return DateComponents(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)
        }

        /**
         * Convenience method that returns a DateComponents from a given Instant
         * that was constructed from UTC based components
         */
        fun fromUTC(instant: Instant): DateComponents {
            return from(instant, TimeZone.UTC)
        }
    }
}
