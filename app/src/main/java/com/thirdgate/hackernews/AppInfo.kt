package com.thirdgate.hackernews

import kotlinx.serialization.Serializable

@Serializable
data class AppInfo(
    val themeId: String = "Hacker News Orange Dark",
    val articleData: ArticleData
)

