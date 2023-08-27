package com.thirdgate.hackernews

import kotlinx.serialization.Serializable

@Serializable
sealed interface ArticleData {

    @Serializable
    data object Loading : ArticleData

    @Serializable
    data class Available(val articles: Map<String, List<ArticleInfo>>) : ArticleData

    @Serializable
    data class Unavailable(val message: String) : ArticleData

    @Serializable
    data class ArticleInfo(
        val id: String,
        val rank: Int,
        val title: String?,
        val by: String,
        val url: String,
        val commentUrl: String,
        val domain: String,
        val score: Int,
        val descendants: Int,
    )
}