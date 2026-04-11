package com.mrbitches.doomsy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DoomsyColorScheme = darkColorScheme(
    primary = VillainOrange,
    onPrimary = DeepBlack,
    primaryContainer = VillainOrangeSubtle,
    secondary = VillainOrange,
    onSecondary = DeepBlack,
    background = Void,
    onBackground = OffWhite,
    surface = Obsidian,
    onSurface = OffWhite,
    surfaceVariant = GunmetalGrey,
    onSurfaceVariant = MutedGrey,
    outline = GlassWhiteBorder,
    outlineVariant = DimGrey,
)

@Composable
fun DoomsyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DoomsyColorScheme,
        typography = DoomsyTypography,
        content = content,
    )
}
