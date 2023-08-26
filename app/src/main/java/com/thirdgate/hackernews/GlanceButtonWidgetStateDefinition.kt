package com.thirdgate.hackernews

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import java.io.File


/**
 * Provides our own definition of "Glance state" using Kotlin serialization.
 */
object GlanceButtonWidgetStateDefinition : GlanceStateDefinition<ArticleData> {

    private const val DATA_STORE_FILENAME = "hackernews_app_data"

    /**
     * Use the same file name regardless of the widget instance to share data between them
     *
     * If you need different state/data for each instance, create a store using the provided fileKey
     */
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, ArticlesRepository.MySerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<ArticleData> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }


}