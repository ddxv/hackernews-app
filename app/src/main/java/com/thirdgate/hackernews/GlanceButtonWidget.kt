/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thirdgate.hackernews

import ApiService
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.background
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle


object GlanceButtonWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val articleType = "top"
        val repository = ArticlesRepository(ApiService(), context)
        val topArticlesMap = repository.loadArticlesFromPreferences(articleType)
        provideContent { MyContent(context, topArticlesMap, articleType) }
    }


    @Composable
    private fun MyContent(
        context: Context,
        topArticlesMap: Map<String, Map<String, Any>>,
        articleType: String,
    ) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .appWidgetBackground()
                    .background(
                        day = GlanceTheme.colors.background.getColor(context),
                        night = GlanceTheme.colors.onBackground.getColor(context)
                    )
                    .appWidgetBackgroundCornerRadius()
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.glances_button_title) + ": $articleType",
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.primary
                    ),
                )

                LazyColumn(
                    //verticalArrangement = Arrangement.spacedBy(8.dp)
                    modifier = GlanceModifier.fillMaxSize()

                ) {
                    val itemsList = topArticlesMap.values.toList()
                    itemsIndexed(itemsList) { _, article ->
                        Row(
                            modifier = GlanceModifier.background(
                                day = Color.White,
                                night = Color.White
                            ).fillMaxSize().padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                        ) {
                            Row(
                                modifier = GlanceModifier.background(
                                    day = Color.LightGray,
                                    night = Color.LightGray
                                ).fillMaxSize().padding(vertical = 2.dp)
                            ) {

                                //Log.i("looping_glances_widget", "$key = $value")
                                var myButton = ArticleFormatter.makeArticleButton(
                                    article,
                                    context
                                )
                                val webIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(article["url"].toString()))
                                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                Button(
                                    text = myButton.text.toString(),
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    onClick = { context.startActivity(webIntent) },
                                )
                                Column(
                                    modifier = GlanceModifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "${article["rank"]}. ${article["title"]}",
                                        modifier = GlanceModifier,
                                        style = TextStyle(fontSize = 12.sp)
                                    )
                                    Text(
                                        text = "${article["score"]} points by: ${article["by"]} | ${article["descendants"]} comments",
                                        modifier = GlanceModifier,
                                        style = TextStyle(fontSize = 10.sp)
                                    )
//                                Spacer(
//                                    modifier = GlanceModifier.height(4.dp)
//                                        .background(day = Color.White, night = Color.White)
//                                )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


class GlanceButtonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = GlanceButtonWidget
}
