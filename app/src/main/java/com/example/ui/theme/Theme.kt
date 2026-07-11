package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTurquoise,
    secondary = SecondaryTeal,
    tertiary = TertiaryTeal,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    surfaceVariant = DarkSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme as requested
    dynamicColor: Boolean = false, // Disable dynamic colors to keep turquoise accents
    content: @Composable () -> Unit
) {
    // We always use the custom DarkColorScheme to ensure the requested dark theme with turquoise accents
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
