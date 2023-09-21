package com.thirdgate.hackernews

import kotlinx.serialization.Serializable

@Serializable
data class AppInfo(
    val themeId: String = "Hacker News Orange Light",
    val articleData: ArticleData,
    val fontSizePreference: String = "medium",
    val browserPreference: String = "inapp"
)

