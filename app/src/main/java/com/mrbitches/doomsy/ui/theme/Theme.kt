package com.mrbitches.doomsy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DoomsyColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepBlack,
    secondary = BrushedSilver,
    onSecondary = DeepBlack,
    background = DeepBlack,
    onBackground = OffWhite,
    surface = GunmetalGrey,
    onSurface = OffWhite,
    surfaceVariant = GunmetalLight,
    onSurfaceVariant = MutedGrey,
    outline = GlassWhiteBorder,
)

@Composable
fun DoomsyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DoomsyColorScheme,
        typography = DoomsyTypography,
        content = content,
    )
}
