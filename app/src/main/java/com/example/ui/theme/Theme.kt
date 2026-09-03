package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FreshGreenLight,
    onPrimary = Color.Black,
    primaryContainer = FreshGreenDark,
    onPrimaryContainer = Color.White,
    secondary = AccentWarmOrange,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceElevated,
    onBackground = Color(0xFFF9FAFB),
    onSurface = Color(0xFFF9FAFB)
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FreshGreenContainer,
    onPrimaryContainer = OnFreshGreenContainer,
    secondary = AccentWarmOrange,
    onSecondary = Color.White,
    background = MintBackground,
    surface = SurfacePureWhite,
    surfaceVariant = SurfaceElevated,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = SoftBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branded green look
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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
