package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ArticlesRepository {

    val Context.dataStore: DataStore<ArticleData> by dataStore(
        fileName = "article_data.pb",
        serializer = MySerializer
    )

    private val apiService = ApiService()

    // Create MutableState internally
    private val _topArticles = mutableStateOf<ArticleData>(ArticleData.Loading)
    private val _bestArticles = mutableStateOf<ArticleData>(ArticleData.Loading)
    private val _newArticles = mutableStateOf<ArticleData>(ArticleData.Loading)

    // Expose read-only State
    val topArticles: State<ArticleData> = _topArticles
    val bestArticles: State<ArticleData> = _bestArticles
    val newArticles: State<ArticleData> = _newArticles

    suspend fun fetchArticles(articleType: String, page: Int = 1): ArticleData {
        val articles: Map<String, Map<String, Any>> = apiService.getArticles(articleType, page)
        Log.i("ArticlesRepository", "ApiService returned: articles {${articles}}")


        val articleList = articles.map { (id, articleMap) ->
            ArticleData.ArticleInfo(
                id = id,
                title = articleMap["title"] as String,
                url = articleMap["url"] as String,
                commentUrl = "https://news.ycombinator.com/item?id=$id",
                domain = articleMap["domain"] as String,
                by = articleMap["by"] as String,
                score = (articleMap["score"] as? Double ?: -1.0).toInt(),
                rank = (articleMap["rank"] as? Double ?: -1.0).toInt(),
                descendants = (articleMap["descendants"] as? Double ?: -1.0).toInt()
            )


        }

        val myArticleData: Map<String, List<ArticleData.ArticleInfo>> =
            mapOf(articleType to articleList)

        when (articleType) {
            "top" -> _topArticles.value = ArticleData.Available(myArticleData)
            "new" -> _newArticles.value = ArticleData.Available(myArticleData)
            "best" -> _bestArticles.value = ArticleData.Available(myArticleData)
        }

        return ArticleData.Available(myArticleData)
    }


    /**
     * Custom serializer for ArticleData using Json.
     */
    object MySerializer : Serializer<ArticleData> {
        override val defaultValue = ArticleData.Loading

        override suspend fun readFrom(input: InputStream): ArticleData {
            return try {
                Json.decodeFromString(
                    ArticleData.serializer(),
                    input.readBytes().decodeToString()
                )
            } catch (exception: SerializationException) {
                throw CorruptionException("Could not read article data: ${exception.message}")
            }
        }

        override suspend fun writeTo(t: ArticleData, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(ArticleData.serializer(), t).encodeToByteArray()
                )
            }
        }
    }

}