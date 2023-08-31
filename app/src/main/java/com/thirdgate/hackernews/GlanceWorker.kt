package com.thirdgate.hackernews

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
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

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.UPDATE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
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

        // Update state with new data
        glanceIds.forEach { glanceId ->
            try {
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
                            articleData = ArticleData.Loading,
                            widgetGlanceId = widgetInfo.widgetGlanceId,
                            articleType = widgetInfo.articleType,
                            themeId = widgetInfo.themeId
                        )
                    }
                )
                GlanceButtonWidget().update(context, glanceId)
                updateAppWidgetState(
                    context = context,
                    glanceId = glanceId,
                    definition = GlanceButtonWidgetStateDefinition()
                ) { widgetInfo ->
                    Log.i(
                        "GlanceWidgetWorker",
                        "LoopWidgets: glanceId: $glanceId, Fetch articles "
                    )
                    WidgetInfo(
                        articleData = ArticlesRepository.fetchArticles(
                            widgetInfo.articleType,
                            page = 1
                        ),
                        widgetGlanceId = widgetInfo.widgetGlanceId,
                        articleType = widgetInfo.articleType,
                        themeId = widgetInfo.themeId
                    )
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
                            themeId = widgetInfo.themeId
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


    /**
     * Update the state of all widgets and then force update UI
     */
    private suspend fun updateAllAppWidgetStates(
        glanceIds: List<GlanceId>,
        newWidgetInfo: WidgetInfo
    ) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = GlanceButtonWidgetStateDefinition(),
                glanceId = glanceId,
                updateState = { newWidgetInfo }
            )
        }
        GlanceButtonWidget().updateAll(context)
    }
}