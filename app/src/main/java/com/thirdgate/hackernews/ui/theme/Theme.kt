package com.thirdgate.hackernews.ui.theme


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.thirdgate.hackernews.R


@Composable
fun MyAppTheme(theme: String, content: @Composable () -> Unit) {


    val colors = when (theme) {
        stringResource(R.string.cyberpunk_dark) -> CyberpunkDarkColorPalette()
        stringResource(R.string.cyberpunk_light) -> CyberpunkLightColorPalette()
        stringResource(R.string.darcula) -> DarculaColorPalette()
        stringResource(R.string.lavender_light) -> LavenderLightColorPalette()
        stringResource(R.string.lavender_dark) -> LavenderDarkColorPalette()
        stringResource(R.string.crystal_blue) -> CrystalBlueColorPalette()
        stringResource(R.string.solarized_light) -> SolarizedLightColorPalette()
        stringResource(R.string.solarized_dark) -> SolarizedDarkColorPalette()
        stringResource(R.string.hacker_news_orange_light) -> HackerNewsOrangeLightColorPalette()
        stringResource(R.string.hacker_news_orange_dark) -> HackerNewsOrangeDarkColorPalette()
        stringResource(R.string.default_theme) -> HackerNewsOrangeLightColorPalette()
        else -> HackerNewsOrangeLightColorPalette()
    }


    MaterialTheme(
        colors = colors,
        content = content
    )
}



