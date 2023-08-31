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
import androidx.glance.material.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.thirdgate.hackernews.ui.theme.CrystalBlueColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkDarkColorPalette
import com.thirdgate.hackernews.ui.theme.CyberpunkLightColorPalette
import com.thirdgate.hackernews.ui.theme.DarculaColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeDarkColorPalette
import com.thirdgate.hackernews.ui.theme.HackerNewsOrangeLightColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderDarkColorPalette
import com.thirdgate.hackernews.ui.theme.LavenderLightColorPalette
import com.thirdgate.hackernews.ui.theme.SolarizedDarkColorPalette
import com.thirdgate.hackernews.ui.theme.SolarizedLightColorPalette


class GlanceButtonWidget : GlanceAppWidget() {

    override val stateDefinition = GlanceButtonWidgetStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        Log.i("GlanceButtonWidget", "provideGlance started glanceId: $id")
        provideContent {
            MyContent(context, id)
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
        id: GlanceId
    ) {
        val widgetInfo = currentState<WidgetInfo>()
        val articleData = widgetInfo.articleData
        val themeId = widgetInfo.themeId
        val articleType = widgetInfo.articleType
        // Shouldn't use, just for checking
        val _wiGI = widgetInfo.widgetGlanceId

        Log.i("GlanceButtonWidget", "Widget: $id  MyContent: themeId: $themeId wiGlanceID: $_wiGI ")

        val themes = mapOf(
            LocalContext.current.getString(R.string.cyberpunk_dark) to CyberpunkDarkColorPalette(),
            LocalContext.current.getString(R.string.cyberpunk_light) to CyberpunkLightColorPalette(),
            LocalContext.current.getString(R.string.darcula) to DarculaColorPalette(),
            LocalContext.current.getString(R.string.lavender_light) to LavenderLightColorPalette(),
            LocalContext.current.getString(R.string.lavender_dark) to LavenderDarkColorPalette(),
            LocalContext.current.getString(R.string.crystal_blue) to CrystalBlueColorPalette(),
            LocalContext.current.getString(R.string.solarized_light) to SolarizedLightColorPalette(),
            LocalContext.current.getString(R.string.solarized_dark) to SolarizedDarkColorPalette(),
            LocalContext.current.getString(R.string.hacker_news_orange_light) to HackerNewsOrangeLightColorPalette(),
            LocalContext.current.getString(R.string.hacker_news_orange_dark) to HackerNewsOrangeDarkColorPalette(),
            LocalContext.current.getString(R.string.default_theme) to HackerNewsOrangeLightColorPalette(),
        )


        val myColors = themes[themeId] ?: HackerNewsOrangeLightColorPalette()

        GlanceTheme(colors = ColorProviders(myColors)) {
            when (articleData) {
                ArticleData.Loading -> {
                    Log.i("GlanceButtonWidget", "Widget: $id ArticleData.Loading")
                    AppWidgetBox(contentAlignment = Alignment.Center) {
                        Column {
                            Image(
                                provider = ImageProvider(R.drawable.round_refresh_24),
                                modifier = GlanceModifier.clickable(
                                    onClick = actionRunCallback<RefreshAction>()
                                ).padding(8.dp),
                                contentDescription = "Refresh"
                            )
                            CircularProgressIndicator()
                        }
                    }
                }

                is ArticleData.Available -> {
                    Log.i("GlanceButtonWidget", "Widget $id ArticleData.Available")
                    MyActualContent(
                        context = context,
                        articleType = articleType,
                        articleData = articleData
                    )

                }

                is ArticleData.Unavailable -> {
                    Log.i("GlanceButtonWidget", "Widget $id ArticleData.UnAvailable")
                    ContentNotAvailable()
                }
            }
        }
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
                            //Log.i("looping_glances_widget", "title: ${article.title}")
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
