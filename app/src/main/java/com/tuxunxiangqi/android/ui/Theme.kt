package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Material 3 theme. The palette is taken from the app icon
 * artwork: rice-paper cream, ink black and vermilion red, so
 * the icon and the interface speak the same language.
 */
val PaperCream = Color(0xFFF6F0E2)
val PaperLight = Color(0xFFFCF8ED)
val InkBlack = Color(0xFF1C1814)
val Vermilion = Color(0xFFB33A1F)
val VermilionDeep = Color(0xFF8F2C15)
val WalnutBrown = Color(0xFF6F4C2A)

private val LightScheme = lightColorScheme(
    primary = Vermilion,
    onPrimary = Color(0xFFFFF7EA),
    primaryContainer = Color(0xFFF3D9CB),
    onPrimaryContainer = VermilionDeep,
    secondary = WalnutBrown,
    onSecondary = Color(0xFFFFF7EA),
    secondaryContainer = Color(0xFFEADFC6),
    onSecondaryContainer = Color(0xFF4A3A1E),
    background = PaperCream,
    onBackground = InkBlack,
    surface = PaperLight,
    onSurface = InkBlack,
    surfaceVariant = Color(0xFFEEE4CC),
    onSurfaceVariant = Color(0xFF554B38),
    outline = Color(0xFFCBBD9C),
    outlineVariant = Color(0xFFE3D7BC)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFE07856),
    onPrimary = Color(0xFF4A1507),
    primaryContainer = Color(0xFF6E2412),
    onPrimaryContainer = Color(0xFFF6CDBB),
    secondary = Color(0xFFD9B98A),
    onSecondary = Color(0xFF3A2A12),
    background = Color(0xFF171310),
    onBackground = Color(0xFFEFE6D2),
    surface = Color(0xFF211B16),
    onSurface = Color(0xFFEFE6D2),
    surfaceVariant = Color(0xFF38302A),
    onSurfaceVariant = Color(0xFFCFC2A8),
    outline = Color(0xFF5A5044),
    outlineVariant = Color(0xFF3A332B)
)

@Composable
fun JiangTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content
    )
}
