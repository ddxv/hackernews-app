package com.thirdgate.hackernews

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.thirdgate.hackernews.data.repository.ArticlesRepository
import com.thirdgate.hackernews.data.repository.ArticlesRepository.dataStore
import com.thirdgate.hackernews.presentation.ui.theme.MyAppTheme
import com.thirdgate.hackernews.widget.BrowserGroup
import com.thirdgate.hackernews.widget.FontSizeGroup
import com.thirdgate.hackernews.widget.ThemeGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = this


        setContent {
            var currentTheme by remember { mutableStateOf("Default") }
            var currentBrowserPreference by remember { mutableStateOf("inapp") }
            var currentFontSize by remember { mutableStateOf("medium") }

            LaunchedEffect(Unit) {
                val appSettings = context.dataStore.data.first()
                currentTheme = appSettings.themeId
                currentBrowserPreference = appSettings.browserPreference
                currentFontSize = appSettings.fontSizePreference
                Log.i(
                    "SettingsActivity",
                    "Read out theme=$currentTheme, bp=$currentBrowserPreference"
                )
            }

            MyAppTheme(theme = currentTheme) {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onThemeChanged = { selectedTheme ->
                        currentTheme = selectedTheme
                        lifecycleScope.launch {
                            ArticlesRepository.writeTheme(context, selectedTheme)
                        }
                    },
                    currentBrowserPreference = currentBrowserPreference,
                    onBrowserChanged = { selectedBrowser ->
                        currentBrowserPreference = selectedBrowser
                        lifecycleScope.launch {
                            ArticlesRepository.writeBrowserPreference(context, selectedBrowser)
                        }
                    },
                    currentFontSize = currentFontSize,
                    onFontSizeChanged = { selectedFontSize ->
                        currentFontSize = selectedFontSize
                        lifecycleScope.launch {
                            ArticlesRepository.writeFontSizePreference(context, selectedFontSize)
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun SettingsScreen(
        currentTheme: String,
        onThemeChanged: (String) -> Unit,
        currentBrowserPreference: String,
        onBrowserChanged: (String) -> Unit,
        currentFontSize: String,
        onFontSizeChanged: (String) -> Unit

    ) {
        LazyColumn(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxSize()
        ) {
            items(1) {
                ThemeGroup(selectedTheme = currentTheme, onSelectedChanged = onThemeChanged)
                FontSizeGroup(
                    selectedFontSize = currentFontSize,
                    onSelectedChanged = onFontSizeChanged
                )
                BrowserGroup(
                    selectedBrowser = currentBrowserPreference,
                    onSelectedChanged = onBrowserChanged
                )
            }

        }
    }


}
