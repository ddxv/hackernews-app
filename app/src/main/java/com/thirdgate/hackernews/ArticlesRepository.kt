package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ArticlesRepository(private val apiService: ApiService, private val context: Context) {

    suspend fun fetchArticles(articleType: String, page: Int = 1): Map<String, Any> {
        val articles: Map<String, Any> = apiService.getArticles(articleType, page)
        Log.i("ArticlesRepository", "ApiService returned: articles {${articles.size}}")
        return articles

    }

    fun saveArticlesToPreferences(articleType: String, data: Map<String, Any>) {
        val sharedPreferences =
            context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        Log.i("repositoryForWidget", "Save this data: $data")
        val jsonString = JSONObject(data).toString()
        sharedPreferences.edit().putString(articleType, jsonString).apply()
    }


    fun loadArticlesFromPreferences(articleType: String): Map<String, Map<String, Any>> {
        val sharedPreferences =
            context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(articleType, null) ?: return mutableMapOf()

        val map = mutableMapOf<String, Map<String, Any>>()
        try {
            val jsonObject = JSONObject(jsonString)
            jsonObject.keys().forEach { key ->
                val innerJsonObject = jsonObject.getJSONObject(key)
                val innerMap = mutableMapOf<String, Any>()
                innerJsonObject.keys().forEach { innerKey ->
                    innerMap[innerKey] = innerJsonObject[innerKey]
                }
                map[key] = innerMap
            }
        } catch (e: JSONException) {
            Log.e(
                "Repository.loadArticlesFromPreferences",
                "Failed to parse JSON for articleType $articleType"
            )
        }
        return map
    }


}