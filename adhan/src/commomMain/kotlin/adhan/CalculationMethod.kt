package org.adhan

/**
 * Standard calculation methods for calculating prayer times.
 */
enum class CalculationMethod {
    /**
     * Muslim World League
     * Uses Fajr angle of 18 and an Isha angle of 17
     */
    MuslimWorldLeague,

    /**
     * Egyptian General Authority of Survey
     * Uses Fajr angle of 19.5 and an Isha angle of 17.5
     */
    Egyptian,

    /**
     * University of Islamic Sciences, Karachi
     * Uses Fajr angle of 18 and an Isha angle of 18
     */
    Karachi,

    /**
     * Umm al-Qura University, Makkah
     * Uses a Fajr angle of 18.5 and an Isha angle of 90. Note: You should add a +30 minute custom
     * adjustment of Isha during Ramadan.
     */
    UmmAlQura,

    /**
     * The Gulf Region
     * Uses Fajr and Isha angles of 18.2 degrees.
     */
    Dubai,

    /**
     * Moonsighting Committee
     * Uses a Fajr angle of 18 and an Isha angle of 18. Also uses seasonal adjustment values.
     */
    MoonSightingCommittee,

    /**
     * Referred to as the ISNA method
     * This method is included for completeness, but is not recommended.
     * Uses a Fajr angle of 15 and an Isha angle of 15.
     */
    NorthAmerica,

    /**
     * Kuwait
     * Uses a Fajr angle of 18 and an Isha angle of 17.5
     */
    Kuwait,

    /**
     * Qatar
     * Modified version of Umm al-Qura that uses a Fajr angle of 18.
     */
    Qatar,

    /**
     * Singapore
     * Uses a Fajr angle of 20 and an Isha angle of 18
     */
    Singapore,

    /**
     * The default value for initialization. Sets a Fajr angle of 0 and an Isha angle of 0.
     */
    Other;

    fun getParameters(): CalculationParameters {
        return when (this) {
            MuslimWorldLeague -> CalculationParameters(18.0, 17.0, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 1, 0, 0, 0))
            Egyptian -> CalculationParameters(19.5, 17.5, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 1, 0, 0, 0))
            Karachi -> CalculationParameters(18.0, 18.0, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 1, 0, 0, 0))
            UmmAlQura -> CalculationParameters(18.5, 90.0, this)
            Dubai -> CalculationParameters(18.2, 18.2, this)
                .withMethodAdjustments(PrayerAdjustments(0, -3, 3, 3, 3, 0))
            MoonSightingCommittee -> CalculationParameters(18.0, 18.0, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 5, 0, 3, 0))
            NorthAmerica -> CalculationParameters(15.0, 15.0, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 1, 0, 0, 0))
            Kuwait -> CalculationParameters(18.0, 17.5, this)
            Qatar -> CalculationParameters(18.0, 90.0, this)
            Singapore -> CalculationParameters(20.0, 18.0, this)
                .withMethodAdjustments(PrayerAdjustments(0, 0, 1, 0, 0, 0))
            Other -> CalculationParameters(0.0, 0.0, this)
        }
    }
}
