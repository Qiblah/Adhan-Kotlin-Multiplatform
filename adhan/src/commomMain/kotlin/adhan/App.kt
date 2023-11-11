package org.adhan

fun mainn() {
    val coordinates = Coordinates(35.8255915, 10.608406)

    val now = Clock.System.now()
    val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateComponents = DateComponents(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth)
    val parameters = CalculationMethod.MuslimWorldLeague.getParameters()

    val prayerTimes = PrayerTimes(dateComponents, coordinates, parameters)

    println("Fajr: ${prayerTimes.fajr?.time}")
    println("Sunrise: ${prayerTimes.sunrise?.time}")
    println("Dhuhr: ${prayerTimes.dhuhr?.time}")
    println("Asr: ${prayerTimes.asr?.time}")
    println("Maghrib: ${prayerTimes.maghrib?.time}")
    println("Isha: ${prayerTimes.isha?.time}")
}