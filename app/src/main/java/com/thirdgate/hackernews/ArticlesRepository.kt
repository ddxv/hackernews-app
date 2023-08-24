package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.util.Log
import org.json.JSONObject

class ArticlesRepository(private val apiService: ApiService, private val context: Context) {

    suspend fun fetchArticles(articleType: String, page: Int = 1): Map<String, Any> {
        return apiService.getArticles(articleType, page)
    }

    fun saveArticlesToPreferences(articleType: String, data: Map<String, Any>) {
        val sharedPreferences =
            context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        Log.i("repository", "$data")
        val jsonString = JSONObject(data).toString()
        sharedPreferences.edit().putString(articleType, jsonString).apply()
    }

    fun loadArticlesFromPreferences(articleType: String): Map<String, Any>? {
        val sharedPreferences =
            context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(articleType, null) ?: return null
        val jsonObject = JSONObject(jsonString)

        val map = mutableMapOf<String, Any>()
        jsonObject.keys().forEach { key ->
            map[key] = jsonObject[key]
        }
        return map
    }
}