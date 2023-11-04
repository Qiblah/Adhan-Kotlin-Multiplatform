package org.adhan

/**
 * Parameters used for PrayerTime calculation customization
 *
 * Note that, for many cases, you can use [CalculationMethod.getParameters] to get a
 * pre-computed set of calculation parameters depending on one of the available
 * [CalculationMethod].
 */
class CalculationParameters {

    /**
     * The method used to do the calculation
     */
    var method = CalculationMethod.Other

    /**
     * The angle of the sun used to calculate fajr
     */
    var fajrAngle: Double = 0.0

    /**
     * The angle of the sun used to calculate isha
     */
    var ishaAngle: Double = 0.0

    /**
     * Minutes after Maghrib (if set, the time for Isha will be Maghrib plus ishaInterval)
     */
    var ishaInterval: Int = 0

    /**
     * The madhab used to calculate Asr
     */
    var madhab = Madhab.Shafi

    /**
     * Rules for placing bounds on Fajr and Isha for high latitude areas
     */
    var highLatitudeRule = HighLatitudeRule.MiddleOfTheNight

    /**
     * Used to optionally add or subtract a set amount of time from each prayer time
     */
    var adjustments = PrayerAdjustments()

    /**
     * Used for method adjustments
     */
    var methodAdjustments = PrayerAdjustments()

    /**
     * Generate CalculationParameters from angles
     * @param fajrAngle the angle for calculating fajr
     * @param ishaAngle the angle for calculating isha
     */
    constructor(fajrAngle: Double, ishaAngle: Double) {
        this.fajrAngle = fajrAngle
        this.ishaAngle = ishaAngle
    }

    /**
     * Generate CalculationParameters from fajr angle and isha interval
     * @param fajrAngle the angle for calculating fajr
     * @param ishaInterval the amount of time after maghrib to have isha
     */
    constructor(fajrAngle: Double, ishaInterval: Int) : this(fajrAngle, 0.0) {
        this.ishaInterval = ishaInterval
    }

    /**
     * Generate CalculationParameters from angles and a calculation method
     * @param fajrAngle the angle for calculating fajr
     * @param ishaAngle the angle for calculating isha
     * @param method the calculation method to use
     */
    constructor(fajrAngle: Double, ishaAngle: Double, method: CalculationMethod) : this(fajrAngle, ishaAngle) {
        this.method = method
    }

    /**
     * Generate CalculationParameters from fajr angle, isha interval, and calculation method
     * @param fajrAngle the angle for calculating fajr
     * @param ishaInterval the amount of time after maghrib to have isha
     * @param method the calculation method to use
     */
    constructor(fajrAngle: Double, ishaInterval: Int, method: CalculationMethod) : this(fajrAngle, ishaInterval) {
        this.method = method
    }

    /**
     * Set the method adjustments for the current calculation parameters
     * @param adjustments the prayer adjustments
     * @return this calculation parameters instance
     */
    fun withMethodAdjustments(adjustments: PrayerAdjustments): CalculationParameters {
        this.methodAdjustments = adjustments
        return this
    }

    internal class NightPortions(val fajr: Double, val isha: Double)

    internal fun nightPortions(): NightPortions {
        return when (highLatitudeRule) {
            HighLatitudeRule.MiddleOfTheNight -> NightPortions(1.0 / 2.0, 1.0 / 2.0)
            HighLatitudeRule.SeventhOfTheNight -> NightPortions(1.0 / 7.0, 1.0 / 7.0)
            HighLatitudeRule.TwilightAngle -> NightPortions(fajrAngle / 60.0, ishaAngle / 60.0)
            else -> throw IllegalArgumentException("Invalid high latitude rule")
        }
    }
}
