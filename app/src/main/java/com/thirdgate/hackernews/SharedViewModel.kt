package com.thirdgate.hackernews

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArticlesRepository


    val topArticles = MutableLiveData<ArticleData>()
    val bestArticles = MutableLiveData<ArticleData>()
    val newArticles = MutableLiveData<ArticleData>()

    val topArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val bestArticlePage = MutableLiveData<Int>().apply { value = 1 }
    val newArticlePage = MutableLiveData<Int>().apply { value = 1 }

    //val Context.dataStore by dataStore("article_data.pb", ArticlesRepository.mySerializer)


//    init {
//        // Load initial data from SharedPreferences when ViewModel is created
//        loadInitialDataFromPreferences()
//    }

    fun loadArticlesInSharedViewModel(articleType: String, page: Int = 1) {
        var data: ArticleData
        // Use coroutine to fetch data asynchronously
        viewModelScope.launch {
            try {
                data = repository.fetchArticles(articleType, page)

            } catch (e: Exception) {
                Log.i(
                    "SharedViewModel.fetchArticles",
                    "repository.FetchArticles(API) failed with: $e"
                )
                //loadArticleTypeFromPreferences(articleType)
                return@launch
            }
            when (articleType) {
                "top" -> {
                    topArticles.postValue(data)
                    //repository.saveArticlesToFile("top", data)
                    //dataStore.data
                }

                "new" -> {
                    newArticles.postValue(data)
                    //repository.saveArticlesToFile("new", data)
                }

                "best" -> {
                    bestArticles.postValue(data)
                    //repository.saveArticlesToFile("best", data)
                }
            }
        }
    }

//    private fun loadArticleTypeFromPreferences(articleType: String) {
//        var data: ArticleData
//        context.dataStore.data.first().showCompleted
//        try {
//            val exampleCounterFlow: Flow<Int> = context.settingsDataStore.data
//                .map { settings ->
//                    // The exampleCounter property is generated from the schema.
//                    settings.exampleCounter
//                }
//
//        }
//        when (articleType) {
//            "top" -> {
//                topArticles.postValue(data)
//            }
//
//            "new" -> {
//                newArticles.postValue(data)
//            }
//
//            "best" -> {
//                bestArticles.postValue(data)
//            }
//        }
//    }


}
