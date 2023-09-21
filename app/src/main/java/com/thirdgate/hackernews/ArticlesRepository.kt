package com.thirdgate.hackernews

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration
import java.time.Instant

object ArticlesRepository {

    val Context.dataStore: DataStore<AppInfo> by dataStore(
        fileName = "article_data",
        serializer = MySerializer
    )

    private val apiService = ApiService()

    // Create MutableState internally
    private var _topArticles = mutableStateOf<ArticleData>(ArticleData.Loading)
    private var _bestArticles = mutableStateOf<ArticleData>(ArticleData.Loading)
    private var _newArticles = mutableStateOf<ArticleData>(ArticleData.Loading)

    // Expose read-only State
    val topArticles: State<ArticleData> = _topArticles
    val bestArticles: State<ArticleData> = _bestArticles
    val newArticles: State<ArticleData> = _newArticles

    private var fetchedTopPages = mutableSetOf<Int>()
    private var fetchedBestPages = mutableSetOf<Int>()
    private var fetchedNewPages = mutableSetOf<Int>()

    suspend fun fetchTheme(context: Context): String {
        var myTheme = "default"
        context.dataStore.data
            .map { settings -> settings.themeId }
            .firstOrNull()
            ?.let { myTheme = it }
        Log.i("ArticleRepository", "Read out theme: $myTheme")
        return myTheme
    }

    suspend fun writeTheme(context: Context, themeId: String) {
        Log.i("ArticleRepository", "Write out theme: $themeId")
        context.dataStore.updateData { currentSettings ->
            AppInfo(articleData = currentSettings.articleData, themeId = themeId)
        }
    }


    suspend fun fetchArticles(articleType: String, page: Int = 1): ArticleData {
        Log.i("ArticlesRepository", "Fetching articleType=$articleType")

        if (page == 1) {
            when (articleType) {
                "top" -> {
                    fetchedTopPages = mutableSetOf<Int>()
                    _topArticles.value = ArticleData.Loading
                    fetchedTopPages.add(page)
                }

                "best" -> {
                    fetchedBestPages = mutableSetOf<Int>()
                    _bestArticles.value = ArticleData.Loading
                    fetchedBestPages.add(page)
                }

                "new" -> {
                    fetchedNewPages = mutableSetOf<Int>()
                    _newArticles.value = ArticleData.Loading
                    fetchedNewPages.add(page)
                }
            }
        } else {
            when (articleType) {
                "top" -> {
                    if (fetchedTopPages.contains(page)) return topArticles.value
                    fetchedTopPages.add(page)
                }

                "best" -> {
                    if (fetchedBestPages.contains(page)) return bestArticles.value
                    fetchedBestPages.add(page)
                }

                "new" -> {
                    if (fetchedNewPages.contains(page)) return newArticles.value
                    fetchedNewPages.add(page)
                }
            }
        }

        try {
            val articles: Map<String, Map<String, Any>> =
                apiService.getArticles(articleType, page)
            Log.i("ArticlesRepository", "ApiService returned: articles {${articles}}")

            val fetchedArticleList: List<ArticleData.ArticleInfo> =
                articles.map { (id, articleMap) ->
                    ArticleData.ArticleInfo(
                        id = id,
                        title = articleMap["title"] as String,
                        url = articleMap["url"] as String,
                        commentUrl = "https://news.ycombinator.com/item?id=$id",
                        domain = articleMap["domain"] as String,
                        by = articleMap["by"] as String,
                        score = (articleMap["score"] as? Double ?: -1.0).toInt(),
                        rank = (articleMap["rank"] as? Double ?: -1.0).toInt(),
                        descendants = (articleMap["descendants"] as? Double ?: -1.0).toInt(),
                        time = (articleMap["time"] as? Double ?: -1.0).toInt(),
                    )
                }

            updateArticles(articleType, fetchedArticleList)

            val myArticleData: Map<String, List<ArticleData.ArticleInfo>> =
                mapOf(articleType to fetchedArticleList)

            return ArticleData.Available(myArticleData)
        } catch (e: Exception) {
            return ArticleData.Unavailable(e.message.orEmpty())
        }
    }

    fun convertEpochToRelativeTime(epochSeconds: Long): String {
        val currentInstant = Instant.now()
        val epochInstant = Instant.ofEpochSecond(epochSeconds)
        val timeDifference = Duration.between(epochInstant, currentInstant)

        val days = timeDifference.toDays()
        if (days > 0) {
            if (days < 2) {
                return "$days day ago"
            } else {
                return "$days days ago"
            }
        }

        val hours = timeDifference.toHours()
        if (hours > 0) {
            if (hours < 2) {
                return "$hours hour ago"
            } else {
                return "$hours hours ago"
            }
        }

        val minutes = timeDifference.toMinutes()
        if (minutes < 2) {
            return "$minutes minute ago"
        } else {
            return "$minutes minutes ago"
        }
    }

    private fun updateArticles(
        articleType: String,
        fetchedArticleList: List<ArticleData.ArticleInfo>
    ) {
        val currentArticles = when (articleType) {
            "top" -> (topArticles.value as? ArticleData.Available)?.articles?.get("top")
                ?: emptyList()

            "best" -> (bestArticles.value as? ArticleData.Available)?.articles?.get("best")
                ?: emptyList()

            "new" -> (newArticles.value as? ArticleData.Available)?.articles?.get("new")
                ?: emptyList()

            else -> emptyList()
        }

        val newArticlesSet = currentArticles + fetchedArticleList
        val myArticleData = mapOf(articleType to newArticlesSet)

        when (articleType) {
            "top" -> _topArticles.value = ArticleData.Available(myArticleData)
            "best" -> _bestArticles.value = ArticleData.Available(myArticleData)
            "new" -> _newArticles.value = ArticleData.Available(myArticleData)
        }
    }

    /**
     * Custom serializer for ArticleData using Json.
     */
    object MySerializer : Serializer<AppInfo> {
        override val defaultValue = AppInfo(articleData = ArticleData.Loading, themeId = "default")


        override suspend fun readFrom(input: InputStream): AppInfo {
            return try {
                Json.decodeFromString(
                    AppInfo.serializer(),
                    input.readBytes().decodeToString()
                )
            } catch (exception: SerializationException) {
                throw CorruptionException("Could not read article data: ${exception.message}")
            }
        }

        override suspend fun writeTo(t: AppInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(AppInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }

}