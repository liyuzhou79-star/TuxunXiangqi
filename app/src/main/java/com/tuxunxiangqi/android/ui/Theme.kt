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
    // Tertiary: a muted olive-gold, used sparingly (progress,
    // tertiary accents) so it never fights the vermilion.
    tertiary = Color(0xFF6B6A2F),
    onTertiary = Color(0xFFFFF7EA),
    tertiaryContainer = Color(0xFFE9E4BE),
    onTertiaryContainer = Color(0xFF43421A),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFF7EA),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF7A1A12),
    background = PaperCream,
    onBackground = InkBlack,
    surface = PaperCream,
    onSurface = InkBlack,
    surfaceContainerLowest = PaperLight,
    surfaceContainerLow = Color(0xFFFBF6E9),
    surfaceContainer = Color(0xFFF0E5CB),
    surfaceContainerHigh = Color(0xFFEADDBC),
    surfaceContainerHighest = Color(0xFFE3D2AB),
    surfaceVariant = Color(0xFFEEE4CC),
    onSurfaceVariant = Color(0xFF554B38),
    surfaceTint = Vermilion,
    inverseSurface = Color(0xFF2E2A24),
    inverseOnSurface = Color(0xFFF3ECDB),
    inversePrimary = Color(0xFFF0A58C),
    outline = Color(0xFFCBBD9C),
    outlineVariant = Color(0xFFE3D7BC),
    scrim = Color(0xFF000000)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFE07856),
    onPrimary = Color(0xFF4A1507),
    primaryContainer = Color(0xFF6E2412),
    onPrimaryContainer = Color(0xFFF6CDBB),
    secondary = Color(0xFFD9B98A),
    onSecondary = Color(0xFF3A2A12),
    tertiary = Color(0xFFC4C48A),
    onTertiary = Color(0xFF2E2E10),
    tertiaryContainer = Color(0xFF4A4A22),
    onTertiaryContainer = Color(0xFFE9E7C3),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF171310),
    onBackground = Color(0xFFEFE6D2),
    surface = Color(0xFF171310),
    onSurface = Color(0xFFEFE6D2),
    surfaceContainerLowest = Color(0xFF120E0B),
    surfaceContainerLow = Color(0xFF1E1915),
    surfaceContainer = Color(0xFF2A231B),
    surfaceContainerHigh = Color(0xFF332B21),
    surfaceContainerHighest = Color(0xFF3D332A),
    surfaceVariant = Color(0xFF38302A),
    onSurfaceVariant = Color(0xFFCFC2A8),
    surfaceTint = Color(0xFFE07856),
    inverseSurface = Color(0xFFEFE6D2),
    inverseOnSurface = Color(0xFF2E2A24),
    inversePrimary = Color(0xFF8F2C15),
    outline = Color(0xFF5A5044),
    outlineVariant = Color(0xFF3A332B),
    scrim = Color(0xFF000000)
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
