package com.thirdgate.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidgetManager

class GlanceWidgetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ConfigurationScreen(glanceWidgetId = glanceWidgetId)
        }
    }
}

@Composable
fun ConfigurationScreen(glanceWidgetId: GlanceId) {
    val context = LocalContext.current

//    Button(
//        text = "Set Background Color",
//        action = actionSendBroadcast<MyBroadcastReceiver>(
//            actionParametersOf(
//                "glanceWidgetId" to glanceWidgetId,
//                "backgroundColor" to Color.Red.toArgb()
//            )
//        )
//    )
    // Update the widget
    val manager = GlanceAppWidgetManager(context)
    //GlanceButtonWidget().update(context, glanceWidgetId)
}