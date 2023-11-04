package org.adhan

/**
 * Adjustment value for prayer times, in minutes
 * These values are added (or subtracted) from the prayer time that is calculated before
 * returning the result times.
 */
class PrayerAdjustments(
    var fajr: Int = 0,
    var sunrise: Int = 0,
    var dhuhr: Int = 0,
    var asr: Int = 0,
    var maghrib: Int = 0,
    var isha: Int = 0
)