package com.thirdgate.hackernews

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.launch

class GlanceWidgetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = this
        val glanceWidgetId: GlanceId
        val themeChoice: String = "hackernewsdark"

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

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
                        themeChoice
                    )
                }
            }
//
//            updateAppWidgetState(context = context, glanceId = glanceWidgetId) {
//                it[intPreferencesKey("uid")] = uid
//            }


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
    themeChoice: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Text("Hi: GlanceId: $glanceWidgetId")

    Button(onClick = {
        scope.launch {
            updateAppWidgetState(context = context, glanceId = glanceWidgetId,
                definition = GlanceButtonWidgetStateDefinition, updateState = { widgetInfo ->
                    Log.d("TAAAGGG", "called widget state")
                    WidgetInfo(articleData = widgetInfo.articleData, themeId = themeChoice)
                }
            )

            glanceApp.update(context, glanceWidgetId)
        }
    }) { Text("Cliiick") }


}


@Composable
fun ErrorScreen() {

    Text("Hi: We failed!")

}