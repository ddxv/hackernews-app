package com.thirdgate.hackernews

import ApiService
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArticlesRepository(ApiService(), application)

    val topArticles = MutableLiveData<Map<String, Any>>()
    val bestArticles = MutableLiveData<Map<String, Any>>()
    val newArticles = MutableLiveData<Map<String, Any>>()
    val topArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val bestArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val newArticlePage = MutableLiveData<Int>().apply { value = 1 }

    init {
        // Load initial data from SharedPreferences when ViewModel is created
        loadInitialDataFromPreferences()
    }

    fun fetchArticles(articleType: String, page: Int = 1) {
        // Use coroutine to fetch data asynchronously
        viewModelScope.launch {
            val data = repository.fetchArticles(articleType, page)
            when (articleType) {
                "top" -> {
                    topArticles.value = data
                    repository.saveArticlesToPreferences("top", data)
                }

                "new" -> {
                    newArticles.value = data
                    repository.saveArticlesToPreferences("new", data)
                }

                "best" -> {
                    bestArticles.value = data
                    repository.saveArticlesToPreferences("best", data)
                }

                else -> {} // Handle any other case if needed
            }
        }
    }

    private fun loadInitialDataFromPreferences() {
        sleep(10000)
        topArticles.value = repository.loadArticlesFromPreferences("top")
        newArticles.value = repository.loadArticlesFromPreferences("new")
        bestArticles.value = repository.loadArticlesFromPreferences("best")
    }
}
