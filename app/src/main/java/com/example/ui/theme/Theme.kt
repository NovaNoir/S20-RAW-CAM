package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CameraAppColors(
    val isLight: Boolean,
    val canvasBg: Color,
    val cardSurface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val amberPrimary: Color,
    val amberHighlight: Color,
    val amberContainer: Color,
    val amberGlow: Color,
    val cyanAccent: Color,
    val cyanContainer: Color,
    val greenAccent: Color,
    val redAccent: Color
)

val DarkCameraAppColors = CameraAppColors(
    isLight = false,
    canvasBg = Neutral950,
    cardSurface = Neutral900,
    surfaceVariant = Neutral850,
    border = BorderWhite10,
    borderSubtle = Neutral800,
    textPrimary = PureWhite,
    textSecondary = Neutral200,
    textMuted = Neutral500,
    amberPrimary = Amber500,
    amberHighlight = Amber400,
    amberContainer = AmberContainer,
    amberGlow = AmberGlow,
    cyanAccent = CameraCyan,
    cyanContainer = CameraCyanContainer,
    greenAccent = CameraGreen,
    redAccent = CameraRed
)

val LightCameraAppColors = CameraAppColors(
    isLight = true,
    canvasBg = LightCanvas,
    cardSurface = LightCardSurface,
    surfaceVariant = LightSurfaceVariant,
    border = LightBorder,
    borderSubtle = LightBorderSubtle,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    amberPrimary = LightAmberPrimary,
    amberHighlight = LightAmberHighlight,
    amberContainer = LightAmberContainer,
    amberGlow = LightAmberGlow,
    cyanAccent = LightCyan,
    cyanContainer = LightCyanContainer,
    greenAccent = LightGreen,
    redAccent = LightRed
)

val LocalCameraAppColors = staticCompositionLocalOf { DarkCameraAppColors }

object AppTheme {
    val colors: CameraAppColors
        @Composable
        get() = LocalCameraAppColors.current
}

private val SophisticatedDarkColorScheme = darkColorScheme(
    primary = Amber500,
    onPrimary = Color(0xFF140E02),
    primaryContainer = AmberContainer,
    onPrimaryContainer = Amber400,
    secondary = CameraCyan,
    onSecondary = Color.Black,
    secondaryContainer = CameraCyanContainer,
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = CameraGreen,
    background = Neutral950,
    onBackground = Neutral200,
    surface = Neutral900,
    onSurface = Neutral200,
    surfaceVariant = Neutral850,
    onSurfaceVariant = Neutral400,
    outline = Neutral500,
    outlineVariant = BorderWhite10
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = LightAmberPrimary,
    onPrimary = Color.White,
    primaryContainer = LightAmberContainer,
    onPrimaryContainer = LightAmberHighlight,
    secondary = LightCyan,
    onSecondary = Color.White,
    secondaryContainer = LightCyanContainer,
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = LightGreen,
    background = LightCanvas,
    onBackground = LightTextPrimary,
    surface = LightCardSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextMuted,
    outlineVariant = LightBorder
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SOPHISTICATED_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = if (themeMode.isLight) HighContrastLightColorScheme else SophisticatedDarkColorScheme
    val appColors = if (themeMode.isLight) LightCameraAppColors else DarkCameraAppColors

    CompositionLocalProvider(LocalCameraAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}


