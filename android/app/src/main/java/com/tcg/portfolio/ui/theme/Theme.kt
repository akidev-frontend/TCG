package com.tcg.portfolio.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF03DAC6),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF018786),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFFCF6679),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF945A68),
    onSecondaryContainer = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0xFF888888),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF03DAC6),
    surfaceTint = Color(0xFF03DAC6),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF03DAC6),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF018786),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFCF6679),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF945A68),
    onSecondaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFF03DAC6),
    surfaceTint = Color(0xFF03DAC6),
)

@Composable
fun TCGPortfolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}