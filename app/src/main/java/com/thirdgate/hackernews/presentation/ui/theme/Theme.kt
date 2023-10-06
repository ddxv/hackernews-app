package com.thirdgate.hackernews.presentation.ui.theme


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.thirdgate.hackernews.R


@Composable
fun MyAppTheme(theme: String, content: @Composable () -> Unit) {

    val themes = mapOf(
        stringResource(R.string.cyberpunk_dark) to CyberpunkDarkColorPalette(),
        stringResource(R.string.cyberpunk_light) to CyberpunkLightColorPalette(),
        stringResource(R.string.darcula) to DarculaColorPalette(),
        stringResource(R.string.lavender_light) to LavenderLightColorPalette(),
        stringResource(R.string.lavender_dark) to LavenderDarkColorPalette(),
        stringResource(R.string.crystal_blue) to CrystalBlueColorPalette(),
        stringResource(R.string.solarized_light) to SolarizedLightColorPalette(),
        stringResource(R.string.solarized_dark) to SolarizedDarkColorPalette(),
        stringResource(R.string.hacker_news_orange_light) to HackerNewsOrangeLightColorPalette(),
        stringResource(R.string.hacker_news_orange_dark) to HackerNewsOrangeDarkColorPalette(),
        stringResource(R.string.default_theme) to HackerNewsOrangeLightColorPalette(),
    )


    val colors = themes[theme] ?: HackerNewsOrangeLightColorPalette()

    MaterialTheme(
        colors = colors,
        content = content
    )
}



