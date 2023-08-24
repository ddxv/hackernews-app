package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.util.Log
import org.json.JSONObject

class ArticlesRepository(private val apiService: ApiService, private val context: Context) {

    suspend fun fetchArticles(articleType: String, page: Int = 1): Map<String, Any> {
        val articles: Map<String, Any> = apiService.getArticles(articleType, page)
        saveArticlesToPreferences(articleType, articles)
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
        val jsonString = sharedPreferences.getString(articleType, null)

        val map = mutableMapOf<String, Map<String, Any>>()
        if (jsonString != null) {
            var jsonObject = JSONObject(jsonString)
            jsonObject.keys().forEach { key ->
                val innerJsonObject = jsonObject.getJSONObject(key)
                val innerMap = mutableMapOf<String, Any>()
                innerJsonObject.keys().forEach { innerKey ->
                    innerMap[innerKey] = innerJsonObject[innerKey]
                }
                map[key] = innerMap
            }
            // ... rest of your code
        } else {
            ApiService().getArticlesBlocking(articleType)
            val jsonString = sharedPreferences.getString(articleType, null)
            var jsonObject = JSONObject(jsonString)
            val map = mutableMapOf<String, Map<String, Any>>()
            jsonObject.keys().forEach { key ->
                val innerJsonObject = jsonObject.getJSONObject(key)
                val innerMap = mutableMapOf<String, Any>()
                innerJsonObject.keys().forEach { innerKey ->
                    innerMap[innerKey] = innerJsonObject[innerKey]
                }
                map[key] = innerMap
            }
        }
        return map
    }


}