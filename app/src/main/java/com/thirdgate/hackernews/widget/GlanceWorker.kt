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
import com.thirdgate.hackernews.ArticleData
import com.thirdgate.hackernews.ArticlesRepository
import java.time.Duration

class GlanceWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = GlanceWorker::class.java.simpleName

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
            val requestBuilder = PeriodicWorkRequestBuilder<GlanceWorker>(
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
        val glanceIds = manager.getGlanceIds(GlanceButtonWidget::class.java)

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
                    definition = GlanceButtonWidgetStateDefinition(),
                    glanceId = glanceId,
                    updateState = { widgetInfo ->
                        previousData = widgetInfo.articleData
                        createWidgetInfo(ArticleData.Loading, widgetInfo)
                    }
                )
                GlanceButtonWidget().update(context, glanceId)
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                    definition = GlanceButtonWidgetStateDefinition()
                ) { widgetInfo ->
                    val data = when (val pulledData =
                        ArticlesRepository.fetchArticles(widgetInfo.articleType, page = 1)) {
                        is ArticleData.Available -> pulledData
                        else -> previousData
                            ?: ArticleData.Unavailable("Failed to get new or existing data")
                    }
                    createWidgetInfo(data, widgetInfo)
                }
                GlanceButtonWidget().update(context, glanceId)
                val r = Result.success()
            } catch (e: Exception) {
                Log.i(
                    "GlanceWidgetWorker",
                    "Looptime: Outside StateDefinition: this.glanceId: $glanceId"
                )
                updateAppWidgetState(
                    context = context,
                    definition = GlanceButtonWidgetStateDefinition(),
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
                GlanceButtonWidget().update(context, glanceId)
                if (runAttemptCount < 10) {
                    // Exponential backoff strategy will avoid the request to repeat
                    // too fast in case of failures.
                    val r = Result.retry()
                } else {
                    val r = Result.failure()
                }
            }
        }
        return r
    }
}