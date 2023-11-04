package org.adhan

/**
 * Adjustment value for prayer times, in minutes
 * These values are added (or subtracted) from the prayer time that is calculated before
 * returning the result times.
 */
class PrayerAdjustments(
    var fajr: Int = 0, // Fajr offset in minutes
    var sunrise: Int = 0, // Sunrise offset in minutes
    var dhuhr: Int = 0, // Dhuhr offset in minutes
    var asr: Int = 0, // Asr offset in minutes
    var maghrib: Int = 0, // Maghrib offset in minutes
    var isha: Int = 0 // Isha offset in minutes
) {
    // Primary constructor already sets all offsets to 0 by default
    // Secondary constructor is not needed unless additional logic is required
}