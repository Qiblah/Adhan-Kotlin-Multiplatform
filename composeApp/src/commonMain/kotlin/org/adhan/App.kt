package org.adhan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.adhan.data.DateComponents
import org.adhan.theme.AppTheme
import org.zephyr.app.adhan.PrayerTimes

@Composable
internal fun App() = AppTheme {
    val coordinates = Coordinates(33.531720, 11.114153)

    val now = Clock.System.now()
    val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val dateComponents = DateComponents(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth)
    val parameters = CalculationMethod.MuslimWorldLeague.getParameters()

    val prayerTimes = PrayerTimes(dateComponents, coordinates, parameters)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Qibla Angle: ${Qibla(coordinates).direction}")
        Text(text = "Fajr: ${prayerTimes.fajr?.time}")
        Text(text = "Sunrise: ${prayerTimes.sunrise?.time}")
        Text(text = "Dhuhr: ${prayerTimes.dhuhr?.time}")
        Text(text = "Asr: ${prayerTimes.asr?.time}")
        Text(text = "Maghrib: ${prayerTimes.maghrib?.time}")
        Text(text = "Isha: ${prayerTimes.isha?.time}")
    }
}

internal expect fun openUrl(url: String?)