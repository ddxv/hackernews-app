package com.thirdgate.hackernews

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ArticlesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Enqueue the worker to fetch and update articles every hour
        val workRequest =
            PeriodicWorkRequest.Builder(ArticleUpdateWorker::class.java, 1, TimeUnit.HOURS)
                .build()
        WorkManager.getInstance(context).enqueue(workRequest)

        // Update the widget's appearance for each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Update the widget's appearance using RemoteViews

        
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        Log.i("AppWidgetProvider", "Updating widget views to R.widget_layout")
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.thirdgate.hackernews.ACTION_ARTICLES_UPDATED") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    ArticlesWidgetProvider::class.java
                )
            )
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

}