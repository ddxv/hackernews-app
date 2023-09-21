package com.thirdgate.hackernews

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.thirdgate.hackernews.ui.theme.CrystalBlueColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkDarkColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkLightColorPalette
import com.thirdgate.hackernews.ui.theme.DarculaColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeDarkColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeLightColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderDarkColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderLightColorPalette
import com.thirdgate.hackernews.ui.theme.MyAppTheme
import com.thirdgate.hackernews.ui.theme.SolarizedDarkColorPalette
import com.thirdgate.hackernews.ui.theme.SolarizedLightColorPalette
import kotlinx.coroutines.launch


class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = this

        setContent {
            var currentTheme by remember { mutableStateOf("Default") }

            LaunchedEffect(Unit) {
                currentTheme = ArticlesRepository.fetchTheme(context)
            }

            MyAppTheme(theme = currentTheme) {
                SettingsScreen(currentTheme) { selectedTheme ->
                    currentTheme = selectedTheme
                    lifecycleScope.launch {
                        ArticlesRepository.writeTheme(context, selectedTheme)
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsScreen(currentTheme: String, onThemeChanged: (String) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThemeGroup(selectedTheme = currentTheme, onSelectedChanged = onThemeChanged)
        }
    }


    @OptIn(ExperimentalLayoutApi::class)
    @Preview(showBackground = true)
    @Composable
    fun ThemeGroup(
        selectedTheme: String = stringResource(R.string.hacker_news_orange_light),
        onSelectedChanged: (String) -> Unit = {}
    ) {
        var selectedTheme by remember { mutableStateOf(selectedTheme) }
        val themes = mapOf(
            stringResource(R.string.hacker_news_orange_light) to HackerNewsOrangeLightColorPalette(),
            stringResource(R.string.hacker_news_orange_dark) to HackerNewsOrangeDarkColorPalette(),
            stringResource(R.string.darcula) to DarculaColorPalette(),
            stringResource(R.string.cyberpunk_dark) to CyberpunkDarkColorPalette(),
            stringResource(R.string.cyberpunk_light) to CyberpunkLightColorPalette(),
            stringResource(R.string.lavender_light) to LavenderLightColorPalette(),
            stringResource(R.string.lavender_dark) to LavenderDarkColorPalette(),
            stringResource(R.string.crystal_blue) to CrystalBlueColorPalette(),
            stringResource(R.string.solarized_light) to SolarizedLightColorPalette(),
            stringResource(R.string.solarized_dark) to SolarizedDarkColorPalette(),
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Card(
                backgroundColor = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(8.dp)
            ) {
                Column(Modifier.padding(8.dp)) {
                    Row() {
                        Text(
                            "Select Color Theme:",
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colors.onSecondary,
                            fontSize = 18.sp
                        )
                    }
                    FlowRow {
                        themes.forEach { item ->
                            Button(
                                onClick = {
                                    selectedTheme = item.key
                                    onSelectedChanged(item.key)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (selectedTheme == item.key) MaterialTheme.colors.primary else MaterialTheme.colors.background,
                                    contentColor = if (selectedTheme == item.key) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onBackground
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(item.key.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            }
        }
    }


}
