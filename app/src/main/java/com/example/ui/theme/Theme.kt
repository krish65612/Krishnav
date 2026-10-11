package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4D0023),
    onSecondaryContainer = NeonMagenta,
    tertiary = NeonViolet,
    onTertiary = Color.White,
    background = ObsidianBlack,
    onBackground = TextWhitePrimary,
    surface = ObsidianSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextWhiteSecondary,
    outline = ObsidianBorder,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006B78),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBCEBFF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFFB80053),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF3E0018),
    tertiary = Color(0xFF6B21A8),
    onTertiary = Color.White,
    background = PureWhite,
    onBackground = TextDarkPrimary,
    surface = PureWhiteSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = PureWhiteCard,
    onSurfaceVariant = TextDarkSecondary,
    outline = PureWhiteBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun VibelioTheme(
    darkTheme: Boolean = true, // Default to sleek music Dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun TuneZxTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) = VibelioTheme(darkTheme, content)
