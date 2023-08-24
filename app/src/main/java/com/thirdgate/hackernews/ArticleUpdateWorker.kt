package com.thirdgate.hackernews

import ApiService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ArticleUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    //private val viewModel = SharedViewModel()
    private val repository = ArticlesRepository(ApiService(), applicationContext)
    override suspend fun doWork(): Result {
        // Fetching articles using SharedViewModel's method
        val topArticlesMap = repository.fetchArticles("top")
        val formattedArticles = topArticlesMap.values.map { article ->
            ArticleFormatter.formatArticle(applicationContext, article as Map<String, Any>)
                .toString()
        }
        Log.i("UpdateWorkerWidget", "${formattedArticles.toString()}")

        val sharedPreferences =
            applicationContext.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putStringSet("formattedArticles", formattedArticles.toSet())
            .apply()


        // Create a RemoteViews object for the widget layout
        val views = RemoteViews(applicationContext.packageName, R.layout.widget_layout)
        val intent = Intent(applicationContext, ArticlesWidgetService::class.java)
        views.setRemoteAdapter(R.id.list_view, intent)

        // Notify widget that its ListView data has changed
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                applicationContext,
                ArticlesWidgetProvider::class.java
            )
        )
        appWidgetIds.forEach { appWidgetId ->
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view)
        }

        Log.i("UpdateWorker", "Notify Done")

        notifyArticlesUpdated(applicationContext)


        return Result.success()
    }


    private fun notifyArticlesUpdated(context: Context) {
        val intent = Intent("com.thirdgate.hackernews.ACTION_ARTICLES_UPDATED")
        context.sendBroadcast(intent)
    }
}