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
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

object GlanceButtonWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = ArticlesRepository(ApiService(), context)
        val topArticlesMap = repository.loadArticlesFromPreferences("top")
        provideContent { MyContent(context, topArticlesMap) }

    }

    @Composable
    private fun MyContent(context: Context, topArticlesMap: Map<String, Map<String, Any>>) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .appWidgetBackground()
                    .appWidgetBackgroundCornerRadius()
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.glances_button_title),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.primary
                    ),
                )

                LazyColumn {

                    // Check if the map is not null
                    topArticlesMap?.let { articles ->
                        for ((key, value) in articles) {
                            Log.i("looping_glances_widget", "$key = $value")
                            var myButton = ArticleFormatter.makeArticleButton(
                                value,
                                context
                            )
                            item {
                                Button(
                                    text = myButton.text.toString(),
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    onClick = actionStartActivity<MainActivity>(),
                                )
                            }

                        }
                    }

                    item {
                        Button(
                            text = "XButton2",
                            modifier = GlanceModifier.fillMaxWidth(),
                            onClick = actionStartActivity<MainActivity>()
                        )
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
