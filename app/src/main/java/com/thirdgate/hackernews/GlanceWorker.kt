package com.thirdgate.hackernews

import android.content.Context
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
        val myTheme: String = "default"
        return try {
            // Update state to indicate loading

            setWidgetState(glanceIds, WidgetInfo(articleData = ArticleData.Loading))
            // Update state with new data
            setWidgetState(
                glanceIds,
                WidgetInfo(
                    articleData = ArticlesRepository.fetchArticles("top", page = 1),
                    themeId = myTheme
                )
            )

            Result.success()
        } catch (e: Exception) {
            //setWidgetState(glanceIds, ArticleData.Unavailable(e.message.orEmpty()))
            setWidgetState(
                glanceIds,
                WidgetInfo(
                    articleData = ArticleData.Unavailable(e.message.orEmpty()),
                    themeId = myTheme
                )
            )
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Update the state of all widgets and then force update UI
     */
    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: WidgetInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = GlanceButtonWidgetStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        GlanceButtonWidget().updateAll(context)
    }
}