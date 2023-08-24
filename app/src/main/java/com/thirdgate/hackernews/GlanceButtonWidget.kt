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
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.ToggleableStateKey
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/**
 * Glance widget that showcases how to use:
 * - Actions
 * - Compound buttons
 * - Buttons
 * - AndroidRemoteView
 */
class GlanceButtonWidget : GlanceAppWidget() {


    @SuppressLint("RemoteViewLayout")
    @Composable
    override fun Content() {
        val context = LocalContext.current.applicationContext
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background)
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
                val repository = ArticlesRepository(ApiService(), context)

                val topArticlesMap = repository.loadArticlesFromPreferences("top")

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

private val SelectedKey = ActionParameters.Key<String>("key")

class CompoundButtonAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // The framework automatically sets the value of the toggled action (true/false)
        // Retrieve it using the ToggleableStateKey
        val toggled = parameters[ToggleableStateKey] ?: false
        updateAppWidgetState(context, glanceId) { prefs ->
            // Get which button the action came from
            val key = booleanPreferencesKey(parameters[SelectedKey] ?: return@updateAppWidgetState)
            // Update the state
            prefs[key] = toggled
        }
        GlanceButtonWidget().update(context, glanceId)
    }
}

class GlanceButtonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GlanceButtonWidget()
}
