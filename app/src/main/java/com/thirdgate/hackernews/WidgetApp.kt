package com.thirdgate.hackernews

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class WidgetApp : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, WidgetUpdateService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            context.startService(intent)
        }
    }


    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            articles: Map<String, Any>
        ) {
            val widgetText = "HackerNews Articles" // Example title for your widget
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_app)
            views.setRemoteAdapter(R.id.list_view, Intent(context, WidgetService::class.java))
            views.setTextViewText(R.id.appwidget_text, widgetText)

            // Update the widget with the RemoteViews
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}