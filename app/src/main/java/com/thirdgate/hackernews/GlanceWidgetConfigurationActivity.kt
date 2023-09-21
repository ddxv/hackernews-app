package com.thirdgate.hackernews

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import com.thirdgate.hackernews.ui.theme.CrystalBlueColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkDarkColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkLightColorPalette
import com.thirdgate.hackernews.ui.theme.DarculaColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeDarkColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeLightColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderDarkColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderLightColorPalette
import com.thirdgate.hackernews.ui.theme.SolarizedDarkColorPalette
import com.thirdgate.hackernews.ui.theme.SolarizedLightColorPalette
import kotlinx.coroutines.launch

class GlanceWidgetConfigurationActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = this
        val glanceWidgetId: GlanceId


        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_CANCELED, resultValue)


        fun finishActivity(result: Int) {
            val resultValue =
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(result, resultValue)
            finish()
        }

        // Try block because getGlanceIdBy throws IllegalArgumentException if no GlanceId is found for this appWidgetId.
        try {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            glanceWidgetId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)


            val glanceAppWidget: GlanceButtonWidget = GlanceButtonWidget()
            setContent {
                CompositionLocalProvider(LocalContext provides context) {
                    ConfigurationScreen(
                        glanceWidgetId = glanceWidgetId,
                        glanceAppWidget,
                        ::finishActivity
                    )
                }
            }

            //glanceAppWidget.update(context, glanceWidgetId)
        } catch (e: IllegalArgumentException) {
            Log.d("WidgetConfig", "No GlanceId found for this appWidgetId.")
            setContent {
                CompositionLocalProvider(LocalContext provides context) {
                    ErrorScreen()
                }
            }
        }
    }
}

@Composable
fun ConfigurationScreen(
    glanceWidgetId: GlanceId,
    glanceApp: GlanceButtonWidget,
    finishActivity: (Int) -> Unit
) {
    val context = LocalContext.current

    var widgetInfo by remember {
        mutableStateOf(
            WidgetInfo(
                articleData = ArticleData.Loading,
                widgetGlanceId = glanceWidgetId.toString()
            )
        )
    }

    var isLoaded by remember { mutableStateOf(false) }  // <-- New loading state

    LaunchedEffect(Unit) {
        try {
            widgetInfo = glanceApp.getAppWidgetState<WidgetInfo>(context, glanceWidgetId)
            isLoaded = true  // <-- Update the state once data is loaded
        } catch (e: Exception) {
            Log.w("WidgetConfiguration", "crashed!")
            widgetInfo = WidgetInfo(
                articleData = ArticleData.Loading,
                widgetGlanceId = glanceWidgetId.toString()
            )
        }
    }

    // Based on the loading state, decide to display the UI or a loading spinner.
    if (isLoaded) {
        ConfigurationUI(widgetInfo, glanceApp, glanceWidgetId, finishActivity)
    } else {
        // Show a loading spinner or some placeholder here
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Example loading spinner from Compose
        }
    }
}

@Composable
fun ConfigurationUI(
    widgetInfo: WidgetInfo,
    glanceApp: GlanceButtonWidget,
    glanceWidgetId: GlanceId,
    finishActivity: (Int) -> Unit
) {
    val context = LocalContext.current


    var themeChoice by remember { mutableStateOf(widgetInfo.themeId) }
    var fontSizeChoice by remember { mutableStateOf(widgetInfo.widgetFontSize) }
    var articleType by remember { mutableStateOf(widgetInfo.articleType) }
    var browserChoice by remember { mutableStateOf(widgetInfo.widgetBrowser) }

    LazyColumn {
        items(1) {
            Text("Widget Settings:")
            ArticleGroup(selectedType = articleType,
                onSelectedChanged = { selected -> articleType = selected })
            ThemeGroup(selectedTheme = themeChoice,
                onSelectedChanged = { selected -> themeChoice = selected })
            FontSizeGroup(selectedFontSize = fontSizeChoice,
                onSelectedChanged = { selected -> fontSizeChoice = selected })
            BrowserGroup(
                selectedBrowser = browserChoice,
                onSelectedChanged = { selected -> browserChoice = selected })

            Row {
                FinishButton(
                    context = context,
                    glanceApp = glanceApp,
                    glanceWidgetId = glanceWidgetId,
                    finishActivity = finishActivity,
                    themeChoice = themeChoice,
                    articleType = articleType,
                    fontSizeChoice = fontSizeChoice,
                    browserChoice = browserChoice
                )
            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun BrowserGroup(
    selectedBrowser: String,
    onSelectedChanged: (String) -> Unit = {}
) {

    val browserOptions = listOf(
        "Default Browser" to "system",
        "HackerNews App Browser" to "inapp",
    )

    Column(modifier = Modifier.padding(8.dp)) {
        Card(
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                Row() {
                    Text(
                        "Select Your Browser to Open:",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp
                    )
                }
                Row {
                    browserOptions.forEach { (item, identifier) ->
                        Button(
                            onClick = {
                                onSelectedChanged(identifier)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedBrowser == identifier) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                                contentColor = if (selectedBrowser == identifier) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(item)
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun FontSizeGroup(
    selectedFontSize: String,
    onSelectedChanged: (String) -> Unit = {}
) {
    val fontSizeOptions: List<String> = listOf("small", "medium", "large")

    Column(modifier = Modifier.padding(8.dp)) {
        Card(
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                Row() {
                    Text(
                        "Select Font Size:",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp
                    )
                }
                Row {
                    fontSizeOptions.forEach { item ->
                        Button(
                            onClick = {
                                onSelectedChanged(item)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedFontSize == item) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                                contentColor = if (selectedFontSize == item) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(item.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun ArticleGroup(
    selectedType: String,
    onSelectedChanged: (String) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf(selectedType) }
    val articleTypes: List<String> = listOf("top", "new", "best")

    Column(modifier = Modifier.padding(8.dp)) {
        Card(
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                Row() {
                    Text(
                        "Select Type of Articles:",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp
                    )
                }
                Row {
                    articleTypes.forEach { item ->
                        Button(
                            onClick = {
                                selectedType = item
                                onSelectedChanged(item)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedType == item) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                                contentColor = if (selectedType == item) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(item.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
//@Preview(showBackground = true)
@Composable
fun ThemeGroup(
    selectedTheme: String,
    onSelectedChanged: (String) -> Unit = {}
) {
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
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Card(
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                Row() {
                    Text(
                        "Select Color Theme:",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp
                    )
                }
                FlowRow {
                    themes.forEach { item ->
                        Button(
                            onClick = {
                                onSelectedChanged(item.key)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedTheme == item.key) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                                contentColor = if (selectedTheme == item.key) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
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

@Composable
fun FinishButton(
    context: Context,
    glanceApp: GlanceAppWidget,
    glanceWidgetId: GlanceId,
    finishActivity: (Int) -> Unit,
    themeChoice: String,
    articleType: String,
    fontSizeChoice: String,
    browserChoice: String
) {
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            Log.i(
                "WidgetConfig",
                "$glanceWidgetId: Finish button clicked, set theme to $themeChoice"
            )
            scope.launch {
                updateAppWidgetState(context = context,
                    glanceId = glanceWidgetId,
                    definition = GlanceButtonWidgetStateDefinition(),
                    updateState = { widgetInfo ->
                        WidgetInfo(
                            articleData = ArticlesRepository.fetchArticles(
                                widgetInfo.articleType,
                                page = 1
                            ),
                            themeId = themeChoice,
                            articleType = articleType,
                            widgetGlanceId = glanceWidgetId.toString(),
                            widgetFontSize = fontSizeChoice,
                            widgetBrowser = browserChoice
                        )
                    }
                )
                Log.i("WidgetConfig", "$glanceWidgetId: updateAppWidgetState done")
                glanceApp.update(context, glanceWidgetId)
                Log.i("WidgetConfig", "$glanceWidgetId update done")
                finishActivity(Activity.RESULT_OK)
            }
        }) {
            Text("Finish")
        }
    }
}

@Composable
fun ErrorScreen() {
    Text("Hi: We failed!")
}

