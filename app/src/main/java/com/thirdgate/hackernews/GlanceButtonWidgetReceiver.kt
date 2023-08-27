package com.thirdgate.hackernews

import android.content.Context
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
        GlanceWorker.enqueue(context)
    }

    /**
     * Called when the last instance of this widget is removed.
     * Make sure to cancel all ongoing workers when user remove all widget instances
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        GlanceWorker.cancel(context)
    }
}