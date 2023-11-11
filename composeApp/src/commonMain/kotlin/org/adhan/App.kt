package org.adhan

import androidx.compose.runtime.Composable
import org.adhan.theme.AppTheme

@Composable
internal fun App() = AppTheme {

}

internal expect fun openUrl(url: String?)