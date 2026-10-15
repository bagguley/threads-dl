package com.threadsdl.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Custom dark color palette
private val DarkPrimary = Color(0xFF9C27B0) // Purple accent
private val DarkSecondary = Color(0xFFE91E63) // Pink accent
private val DarkTertiary = Color(0xFF673AB7) // Deep purple

private val DarkBackground = Color(0xFF121212) // Very dark gray
private val DarkSurface = Color(0xFF1E1E1E) // Dark gray
private val DarkSurfaceVariant = Color(0xFF2A2A2A) // Medium dark gray

private val OnDarkPrimary = Color(0xFFFFFFFF)
private val OnDarkSecondary = Color(0xFFFFFFFF)
private val OnDarkBackground = Color(0xFFE0E0E0) // Light gray text
private val OnDarkSurface = Color(0xFFE0E0E0)

val ThreadsDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = OnDarkPrimary,
    primaryContainer = Color(0xFF4A148C),
    onPrimaryContainer = Color(0xFFE1BEE7),

    secondary = DarkSecondary,
    onSecondary = OnDarkSecondary,
    secondaryContainer = Color(0xFF3A3A3A),
    onSecondaryContainer = Color(0xFFE0E0E0),

    tertiary = DarkTertiary,

    background = DarkBackground,
    onBackground = OnDarkBackground,

    surface = DarkSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCACACA),

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF5C2020),
    onErrorContainer = Color(0xFFFFCDD2),

    outline = Color(0xFF404040),
    outlineVariant = Color(0xFF333333)
)
