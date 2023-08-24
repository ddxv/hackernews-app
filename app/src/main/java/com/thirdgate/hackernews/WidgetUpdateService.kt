package com.thirdgate.hackernews

import ApiService
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WidgetUpdateService : JobIntentService() {


    override fun onHandleWork(intent: Intent) {
        val apiService = ApiService()
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        GlobalScope.launch {
            val articles = apiService.getArticles("top")
            WidgetApp.updateAppWidget(
                this@WidgetUpdateService,
                appWidgetManager,
                appWidgetId,
                articles
            )
        }
    }

    companion object {
        private const val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, WidgetUpdateService::class.java, JOB_ID, work)
        }
    }
}
