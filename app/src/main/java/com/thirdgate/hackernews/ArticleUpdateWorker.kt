package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.content.Intent
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

        // Create a RemoteViews object for the widget layout
        val views = RemoteViews(applicationContext.packageName, R.layout.widget_layout)
        // ... (Use the RemoteViews API to populate the ListView in the widget with formattedArticles) ...


        // Since the SharedViewModel now saves articles to SharedPreferences,
        // we don't need to save them again here in the Worker.
        // Instead, we simply notify that articles have been updated.
        notifyArticlesUpdated(applicationContext)

        return Result.success()
    }

    private fun notifyArticlesUpdated(context: Context) {
        val intent = Intent("com.thirdgate.hackernews.ACTION_ARTICLES_UPDATED")
        context.sendBroadcast(intent)
    }
}