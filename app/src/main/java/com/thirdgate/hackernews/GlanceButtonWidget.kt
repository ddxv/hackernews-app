package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle


class GlanceButtonWidget : GlanceAppWidget() {

    override val stateDefinition = GlanceButtonWidgetStateDefinition


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val articleType = "top"

        provideContent {
            GlanceTheme(colors = MyGlanceTheme.colors) {
                MyContent(context, articleType)
            }
        }

    }

    @Composable
    private fun ContentNotAvailable() {
        AppWidgetColumn(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Data not available")
            Button("Refresh", actionRunCallback<RefreshAction>())
            Image(
                provider = ImageProvider(androidx.glance.appwidget.R.drawable.glance_loading_layout_background),
                modifier = GlanceModifier.clickable(
                    onClick = actionRunCallback<RefreshAction>()
                ),
                contentDescription = "Refresh"
            )


        }
    }

    @Composable
    private fun MyContent(
        context: Context,
        articleType: String,
    ) {
        val articleData = currentState<ArticleData>()

        when (articleData) {
            ArticleData.Loading -> {
                AppWidgetBox(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ArticleData.Available -> {
                MyActualContent(
                    context = context,
                    articleType = articleType,
                    articleData = articleData
                )

            }

            is ArticleData.Unavailable -> {
                ContentNotAvailable()

            }
        }
        //}
    }

    @Composable
    private fun MyActualContent(
        context: Context,
        articleType: String,
        articleData: ArticleData.Available
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground().cornerRadius(8.dp)
                .background(
                    day = GlanceTheme.colors.primary.getColor(context),
                    night = GlanceTheme.colors.primary.getColor(context)
                )
        ) {
            Row(
                modifier = GlanceModifier.background(
                    day = GlanceTheme.colors.primary.getColor(
                        context
                    ), night = GlanceTheme.colors.primary.getColor(context)
                ).padding(4.dp).fillMaxWidth()
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.glances_button_title) + ": ${articleType.replaceFirstChar { it.uppercase() }}",
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlanceTheme.colors.onPrimary
                    ),
                )
//                Image(
//                    provider = ImageProvider(R.drawable.round_settings_24),
//                    modifier = GlanceModifier.clickable(
//                        onClick = actionStartActivity<GlanceWidgetConfigurationActivity>()
//                    ),
//                    contentDescription = "Settings"
//                )
                //Spacer(modifier = GlanceModifier.defaultWeight())
                Image(
                    provider = ImageProvider(R.drawable.round_refresh_24),
                    modifier = GlanceModifier.clickable(
                        onClick = actionRunCallback<RefreshAction>()
                    ),
                    contentDescription = "Refresh"
                )
            }

            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()

            ) {
                val myData = articleData.articles[articleType] ?: return@LazyColumn
                val itemsList = myData.toList()
                itemsIndexed(itemsList) { _, article ->
                    Row(
                        modifier = GlanceModifier.background(
                            day = GlanceTheme.colors.background.getColor(context),
                            night = GlanceTheme.colors.background.getColor(context)
                        ).fillMaxSize().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                    ) {
                        Row(
                            modifier = GlanceModifier.background(
                                day = GlanceTheme.colors.background.getColor(context),
                                night = GlanceTheme.colors.background.getColor(context),
                            ).fillMaxSize().padding(vertical = 2.dp)
                        ) {
                            Log.i("looping_glances_widget", "title: ${article.title}")
                            val webIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            Column(
                                modifier = GlanceModifier.padding(horizontal = 8.dp)

                            ) {
                                Text(
                                    text = "${article.rank}. ${article.title}",
                                    modifier = GlanceModifier.clickable(
                                        block = {
                                            makeAClick(
                                                context,
                                                article.url
                                            )
                                        }
                                    ),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Left,
                                        color = GlanceTheme.colors.onBackground
                                    )
                                )
                                Text(
                                    text = "${article.score} points by: ${article.by} | ${article.descendants} comments",
                                    modifier = GlanceModifier.clickable(
                                        block = {
                                            makeAClick(
                                                context,
                                                article.commentUrl
                                            )
                                        }
                                    ),
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Left,
                                        color = GlanceTheme.colors.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun makeAClick(context: Context, url: String) {
    val webIntent =
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(webIntent)
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        GlanceWorker.enqueue(context = context, force = true)
    }
}
