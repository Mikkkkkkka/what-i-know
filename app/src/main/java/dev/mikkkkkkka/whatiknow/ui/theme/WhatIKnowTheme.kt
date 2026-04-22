package dev.mikkkkkkka.whatiknow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E53),
    onPrimary = Color(0xFFF4F3EE),
    primaryContainer = Color(0xFFB8E3D7),
    onPrimaryContainer = Color(0xFF07211C),
    secondary = Color(0xFFC76C3A),
    onSecondary = Color(0xFFFFF6F0),
    background = Color(0xFFF4F1E8),
    onBackground = Color(0xFF1F2523),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF1D2421),
    surfaceVariant = Color(0xFFE0D8CB),
    onSurfaceVariant = Color(0xFF4F5A56),
    outline = Color(0xFF7D8A86),
    error = Color(0xFFB42318),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF8FD4C2),
    onPrimary = Color(0xFF08241E),
    primaryContainer = Color(0xFF174D44),
    onPrimaryContainer = Color(0xFFC4F1E7),
    secondary = Color(0xFFF2A06E),
    onSecondary = Color(0xFF3C1707),
    background = Color(0xFF111715),
    onBackground = Color(0xFFEAF1EC),
    surface = Color(0xFF18201D),
    onSurface = Color(0xFFEAF1EC),
    surfaceVariant = Color(0xFF2E3834),
    onSurfaceVariant = Color(0xFFBEC9C4),
    outline = Color(0xFF87938F),
    error = Color(0xFFFFB4AB),
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
)

@Composable
fun WhatIKnowTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
