package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ArticlesRepository {

    // From doc for Proto DataStore: https://developer.android.com/topic/libraries/architecture/datastore
    val Context.dataStore: DataStore<ArticleData> by dataStore(
        fileName = "article_data.pb",
        serializer = MySerializer
    )


    // From both examples, doesn't match Google docs
    //val Context.dataStore by dataStore("article_data.pb", mySerializer)

    //val articleTypeData

    private val apiService = ApiService()

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
                // map other fields as required
            )
        }


        val myArticleData: Map<String, List<ArticleData.ArticleInfo>> =
            mapOf(articleType to articleList)

        return ArticleData.Available(myArticleData)
    }


//    suspend fun saveArticlesToFile(articleType: String, data: ArticleData) {
//        val key = stringPreferencesKey(articleType)
//        val jsonString = Json.encodeToString(mySerializer, data)
//        dataStore.edit { preferences ->
//            preferences[key] = jsonString
//        }
//    }
//
//    fun loadArticlesFromFile(): Flow<ArticleData> {
//        return articleTypeData.map { jsonString ->
//            val map = mutableMapOf<String, Map<String, Any>>()
//            try {
//                val jsonObject = JSONObject(jsonString)
//                jsonObject.keys().forEach { key ->
//                    val innerJsonObject = jsonObject.getJSONObject(key)
//                    val innerMap = mutableMapOf<String, Any>()
//                    innerJsonObject.keys().forEach { innerKey ->
//                        innerMap[innerKey] = innerJsonObject[innerKey]
//                    }
//                    map[key] = innerMap
//                }
//            } catch (e: JSONException) {
//                Log.e(
//                    "Repository.loadArticlesFromPreferences",
//                    "Failed to parse JSON for articleType"
//                )
//            }
//            map
//        }
//    }

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