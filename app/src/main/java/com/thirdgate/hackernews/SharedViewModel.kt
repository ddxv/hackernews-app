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

    fun fetchArticles(apiService: ApiService) {
        // Use coroutine to fetch data asynchronously
        viewModelScope.launch {
            var data = apiService.getArticles("top")
            topArticles.value = data
            data = apiService.getArticles("new")
            newArticles.value = data
            data = apiService.getArticles("best")
            bestArticles.value = data
        }
    }


}