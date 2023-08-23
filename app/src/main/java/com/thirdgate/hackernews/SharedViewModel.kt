package com.thirdgate.hackernews

import ApiService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class SharedViewModel : ViewModel() {
    val topArticles = MutableLiveData<Map<String, Any>>()
    val bestArticles = MutableLiveData<Map<String, Any>>()
    val newArticles = MutableLiveData<Map<String, Any>>()
    val topArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val bestArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val newArticlePage = MutableLiveData<Int>().apply { value = 1 }

    fun fetchArticles(apiService: ApiService, articleType: String, page: Int = 1) {
        // Use coroutine to fetch data asynchronously
        viewModelScope.launch {
            val data = apiService.getArticles(articleType, page = page)
            when (articleType) {
                "top" -> topArticles.value = data
                "new" -> newArticles.value = data
                "best" -> bestArticles.value = data
                else -> {} // Handle any other case if needed
            }
        }


    }
//
//    fun fetchArticles(apiService: ApiService, articleType: String, page: Int = 1) {
//        viewModelScope.launch {
//            val newData = apiService.getArticles(articleType, page)
//
//            when (articleType) {
//                "top" -> {
//                    val currentData = topArticles.value ?: mapOf()
//                    topArticles.value = currentData + newData
//                }
//
//                "new" -> {
//                    val currentData = newArticles.value ?: mapOf()
//                    newArticles.value = currentData + newData
//                }
//
//                "best" -> {
//                    val currentData = bestArticles.value ?: mapOf()
//                    bestArticles.value = currentData + newData
//                }
//            }
//        }
//   }


}