package com.thirdgate.hackernews.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.thirdgate.hackernews.ArticleData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


/**
 * Provides our own definition of "Glance state" using Kotlin serialization.
 */
class GlanceButtonWidgetStateDefinition : GlanceStateDefinition<WidgetInfo> {

    val DATASTORE_FILE_PREFIX = "hackernews_widget_"

    /**
     * Use the same file name regardless of the widget instance to share data between them
     *
     * If you need different state/data for each instance, create a store using the provided fileKey
     */

    override suspend fun getDataStore(context: Context, fileKey: String) =
        DataStoreFactory.create(
            serializer = MySerializer,
            produceFile = {
                getLocation(context, fileKey)
            }
        )

    override fun getLocation(context: Context, fileKey: String) =
        context.dataStoreFile(DATASTORE_FILE_PREFIX + fileKey.lowercase())

}

/**
 * Custom serializer for ArticleData using Json.
 */
object MySerializer : Serializer<WidgetInfo> {
    override val defaultValue =
        WidgetInfo(
            articleData = ArticleData.Loading,
            themeId = "default",
            widgetGlanceId = "default"
        )

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
