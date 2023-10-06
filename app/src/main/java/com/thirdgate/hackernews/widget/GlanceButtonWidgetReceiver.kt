package com.thirdgate.hackernews.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Handle system events for AppWidgets with the provided GlanceAppWidget instance.
 *
 * Use this class to handle widget lifecycle specific events like onEnable/Disable.
 */
class GlanceButtonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = GlanceButtonWidget()

    /**
     * Called when the first instance of the widget is placed. Since all instances share the same
     * state, we don't need to enqueue a new one for subsequent instances.
     *
     * Note: if you would need to load different data for each instance you could enqueue onUpdate
     * method instead. It's safe to call multiple times because of the unique work + KEEP policy
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.i("Widget", "GlanceButtonWidgetReceiver onEnabled, enqueue time")
        GlanceWorker.enqueue(context, force = true)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Widget", "GlanceButtonWidgetReceiver onReceive!")
        super.onReceive(context, intent)
        when (intent.action) {
            "android.intent.action.MY_PACKAGE_REPLACED" -> {
                Log.i("Widget", "GlanceButtonWidgetReceiver onReceive, replaced time")
                // Trigger widget update
                //GlanceButtonWidget().updateAll()
            }

            "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS" -> {
                Log.i("Widget", "GlanceButtonWidgetReceiver options changed, force refresh")
                // TODO This force reloads on resize. Expensive but fixes issue of initial XML on reinstall...
                GlanceWorker.enqueue(context, force = true)
            }

            else -> {
                Log.i("Widget", "GlanceButtonWidgetReceiver unknown intent: ${intent.action}")
            }
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        Log.i("Widget", "GlanceButtonWidgetReceiver onRestored!")
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }
//
    /**
     * Called when the last instance of this widget is removed.
     * Make sure to cancel all ongoing workers when user remove all widget instances
     */
    override fun onDisabled(context: Context) {
        Log.i("Widget", "GlanceButtonWidgetReceiver onDIsabled!")
        super.onDisabled(context)
        GlanceWorker.cancel(context)
    }
}