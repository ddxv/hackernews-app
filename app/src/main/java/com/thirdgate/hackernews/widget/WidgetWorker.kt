package com.thirdgate.hackernews.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.thirdgate.hackernews.data.model.ArticleData
import com.thirdgate.hackernews.data.repository.ArticlesRepository
import java.time.Duration

class WidgetWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = WidgetWorker::class.java.simpleName

        /**
         * Enqueues a new worker to refresh article data only if not enqueued already
         *
         * Note: if you would like to have different workers per widget instance you could provide
         * the unique name based on some criteria
         *
         * @param force set to true to replace any ongoing work and expedite the request
         */
        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<WidgetWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            Log.i("GlanceWorker", "Enqueued...")

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.UPDATE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
            Log.i("GlanceWorker", "Enqueued finished")
        }

        /**
         * Cancel any ongoing worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(NewsWidget::class.java)

        val r: Result = Result.failure()

        fun createWidgetInfo(data: ArticleData, widgetInfo: WidgetInfo): WidgetInfo {
            return WidgetInfo(
                articleData = data,
                widgetGlanceId = widgetInfo.widgetGlanceId,
                articleType = widgetInfo.articleType,
                themeId = widgetInfo.themeId,
                widgetFontSize = widgetInfo.widgetFontSize,
                widgetBrowser = widgetInfo.widgetBrowser
            )
        }

        // Update state with new data
        glanceIds.forEach { glanceId ->
            var previousData: ArticleData? = null
            try {
                Log.i("GlanceWorker", "forEach glanceId: $glanceId")
                updateAppWidgetState(
                    context = context,
                    definition = NewsWidgetStateDefinition(),
                    glanceId = glanceId,
                    updateState = { widgetInfo ->
                        previousData = widgetInfo.articleData
                        createWidgetInfo(ArticleData.Loading, widgetInfo)
                    }
                )
                NewsWidget().update(context, glanceId)
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                    definition = NewsWidgetStateDefinition()
                ) { widgetInfo ->
                    val data = when (val pulledData =
                        ArticlesRepository.fetchArticles(context, "widget", widgetInfo.articleType, page = 1)) {
                        is ArticleData.Available -> pulledData
                        else -> previousData
                            ?: ArticleData.Unavailable("Failed to get new or existing data")
                    }
                    createWidgetInfo(data, widgetInfo)
                }
                NewsWidget().update(context, glanceId)
                Result.success()
            } catch (e: Exception) {
                Log.i(
                    "GlanceWidgetWorker",
                    "Looptime: Outside StateDefinition: this.glanceId: $glanceId"
                )
                updateAppWidgetState(
                    context = context,
                    definition = NewsWidgetStateDefinition(),
                    glanceId = glanceId,
                    updateState = { widgetInfo ->
                        WidgetInfo(
                            articleData = ArticleData.Unavailable(e.message.orEmpty()),
                            widgetGlanceId = widgetInfo.widgetGlanceId,
                            articleType = widgetInfo.articleType,
                            themeId = widgetInfo.themeId,
                            widgetFontSize = widgetInfo.widgetFontSize,
                            widgetBrowser = widgetInfo.widgetBrowser
                        )
                    }
                )
                NewsWidget().update(context, glanceId)
                if (runAttemptCount < 10) {
                    // Exponential backoff strategy will avoid the request to repeat
                    // too fast in case of failures.
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
        return r
    }
}