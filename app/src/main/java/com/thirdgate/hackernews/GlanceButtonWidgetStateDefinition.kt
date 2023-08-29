package com.thirdgate.hackernews

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream


/**
 * Provides our own definition of "Glance state" using Kotlin serialization.
 */
object GlanceButtonWidgetStateDefinition : GlanceStateDefinition<WidgetInfo> {

    private const val DATA_STORE_FILENAME = "hackernews_widget_data"

    /**
     * Use the same file name regardless of the widget instance to share data between them
     *
     * If you need different state/data for each instance, create a store using the provided fileKey
     */
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, MySerializer)


    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<WidgetInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }


}

/**
 * Custom serializer for ArticleData using Json.
 */
object MySerializer : Serializer<WidgetInfo> {
    override val defaultValue = WidgetInfo(articleData = ArticleData.Loading, themeId = "default")

    override suspend fun readFrom(input: InputStream): WidgetInfo {
        return try {
            Json.decodeFromString(
                WidgetInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read article data: ${exception.message}")
        }
    }

    override suspend fun writeTo(t: WidgetInfo, output: OutputStream) {
        output.use {
            it.write(
                Json.encodeToString(WidgetInfo.serializer(), t).encodeToByteArray()
            )
        }
    }
}
