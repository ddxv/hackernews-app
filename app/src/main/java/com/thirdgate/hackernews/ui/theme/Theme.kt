package com.thirdgate.hackernews.ui.theme


import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color.LightGray,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val CyberpunkLightColorPalette = lightColors(
    primary = CyberpunkLightPrimary,
    primaryVariant = CyberpunkLightPrimaryVariant,
    secondary = CyberpunkLightSecondary,
    background = CyberpunkLightBackground,
    surface = CyberpunkLightSurface,
    onPrimary = CyberpunkLightOnPrimary,
    onSecondary = CyberpunkLightOnSecondary,
    onBackground = CyberpunkLightOnBackground,
    onSurface = CyberpunkLightOnSurface,
)

private val CyberpunkDarkColorPalette = darkColors(
    primary = CyberpunkDarkPrimary,
    primaryVariant = CyberpunkDarkPrimaryVariant,
    secondary = CyberpunkDarkSecondary,
    background = CyberpunkDarkBackground,
    surface = CyberpunkDarkSurface,
    onPrimary = CyberpunkDarkOnPrimary,
    onSecondary = CyberpunkDarkOnSecondary,
    onBackground = CyberpunkDarkOnBackground,
    onSurface = CyberpunkDarkOnSurface,
)

@Composable
fun MyAppTheme(theme: String, content: @Composable() () -> Unit) {
    val colors = when (theme) {
        "Cyberpunk" -> CyberpunkDarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}



