package com.thirdgate.hackernews

import ApiService
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArticlesRepository(ApiService(), application)

    val topArticles = MutableLiveData<Map<String, Any>>()
    val bestArticles = MutableLiveData<Map<String, Any>>()
    val newArticles = MutableLiveData<Map<String, Any>>()
    val topArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val bestArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val newArticlePage = MutableLiveData<Int>().apply { value = 1 }

//    init {
//        // Load initial data from SharedPreferences when ViewModel is created
//        loadInitialDataFromPreferences()
//    }

    fun loadArticlesInSharedViewModel(articleType: String, page: Int = 1) {
        var data: Map<String, Any>
        // Use coroutine to fetch data asynchronously
        viewModelScope.launch {
            try {
                data = repository.fetchArticles(articleType, page)

            } catch (e: Exception) {
                Log.i(
                    "SharedViewModel.fetchArticles",
                    "repository.FetchArticles(API) failed, will try loading from local"
                )
                loadArticleTypeFromPreferences(articleType)
                return@launch
            }
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
            }
        }
    }

    private fun loadArticleTypeFromPreferences(articleType: String) {
        var data: Map<String, Map<String, Any>> = mutableMapOf()
        try {
            data = repository.loadArticlesFromPreferences(articleType)
        } catch (e: Exception) {
            Log.i(
                "SharedViewModel.loadArticleFromPreference",
                "Failed, loading articleType $articleType with error $e"
            )
            return
        }
        when (articleType) {
            "top" -> {
                topArticles.value = data
            }

            "new" -> {
                newArticles.value = data
            }

            "best" -> {
                bestArticles.value = data
            }
        }
    }

//    private fun loadInitialDataFromPreferences() {
//        topArticles.value = repository.loadArticlesFromPreferences("top")
//        newArticles.value = repository.loadArticlesFromPreferences("new")
//        bestArticles.value = repository.loadArticlesFromPreferences("best")
//    }
}
