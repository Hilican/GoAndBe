package com.example.goandbe.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = DeepNavy,
    primaryVariant = GlacialBlue,
    secondary = MountainTeal,
    background = BackgroundCloud,
    surface = SurfaceWhite,
    error = ErrorCoral,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun GoAndBeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        content = content
    )
}