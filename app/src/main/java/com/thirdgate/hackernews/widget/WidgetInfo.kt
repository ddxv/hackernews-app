package com.thirdgate.hackernews.widget

import com.thirdgate.hackernews.data.model.ArticleData
import kotlinx.serialization.Serializable

@Serializable
data class WidgetInfo(
    val themeId: String = "Hacker News Orange Light",
    val articleType: String = "top",
    val articleData: ArticleData,
    val widgetGlanceId: String,
    val widgetFontSize: String = "medium",
    val widgetBrowser: String = "inapp"
)

