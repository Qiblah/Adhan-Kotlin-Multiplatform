package org.adhan

/**
 * Rules for dealing with Fajr and Isha at places with high latitudes
 */
enum class HighLatitudeRule {
    /**
     * Fajr will never be earlier than the middle of the night, and Isha will never be later than
     * the middle of the night.
     */
    MiddleOfTheNight,

    /**
     * Fajr will never be earlier than the beginning of the last seventh of the night, and Isha will
     * never be later than the end of the first seventh of the night.
     */
    SeventhOfTheNight,

    /**
     * Similar to seventhOfTheNight, but instead of 1/7th, the fraction
     * of the night used is fajrAngle / 60 and ishaAngle / 60.
     */
    TwilightAngle
}
