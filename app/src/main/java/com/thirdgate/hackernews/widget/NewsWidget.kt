package com.thirdgate.hackernews.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
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
import androidx.glance.layout.Box
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
import com.thirdgate.hackernews.MainActivity
import com.thirdgate.hackernews.R
import com.thirdgate.hackernews.WebViewActivity
import com.thirdgate.hackernews.data.model.ArticleData
import com.thirdgate.hackernews.presentation.ui.theme.CrystalBlueColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.CyberpunkDarkColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.CyberpunkLightColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.DarculaColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.HackerNewsOrangeDarkColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.HackerNewsOrangeLightColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.LavenderDarkColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.LavenderLightColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.SolarizedDarkColorPalette
import com.thirdgate.hackernews.presentation.ui.theme.SolarizedLightColorPalette


class NewsWidget : GlanceAppWidget() {

    override val stateDefinition = NewsWidgetStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        Log.i("GlanceButtonWidget", "provideGlance started glanceId: $id")
        provideContent {
            MyContent(context, id)
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
        val chosenFontSize = widgetInfo.widgetFontSize
        val chosenBrowser = widgetInfo.widgetBrowser

        var smallFontSize = 10
        var regularFontSize = 12
        var largeFontSize = 18

        when (chosenFontSize) {
            "small" -> {
                largeFontSize -= 2
                regularFontSize -= 2
                smallFontSize -= 2
            }

            "large" -> {
                largeFontSize += 2
                regularFontSize += 2
                smallFontSize += 2
            }
        }

        Log.i("GlanceButtonWidget", "Widget: $id  MyContent: themeId: $themeId ")

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
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground().cornerRadius(8.dp)
                    .background(
                        day = GlanceTheme.colors.primary.getColor(context),
                        night = GlanceTheme.colors.primary.getColor(context)
                    ).clickable(
                        onClick = actionStartActivity<MainActivity>()
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
                            fontSize = largeFontSize.sp,
                            color = GlanceTheme.colors.onPrimary
                        ),
                    )
                    Image(
                        provider = ImageProvider(R.drawable.round_refresh_24),
                        modifier = GlanceModifier.clickable(
                            onClick = actionRunCallback<RefreshAction>()
                        ),
                        contentDescription = "Refresh",
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)

                    )
                }


                when (articleData) {
                    ArticleData.Loading -> {
                        Log.i("GlanceButtonWidget", "Widget: $id ArticleData.Loading")
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = GlanceModifier.background(
                                day = GlanceTheme.colors.background.getColor(context),
                                night = GlanceTheme.colors.background.getColor(context)
                            ).fillMaxSize()
                        )
                        {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Text(
                                    "Data loading ...", style = TextStyle(
                                        fontSize = regularFontSize.sp,
                                        textAlign = TextAlign.Center,
                                        color = GlanceTheme.colors.onBackground
                                    ), modifier = GlanceModifier.padding(20.dp)
                                )
                                Button("Refresh", actionRunCallback<RefreshAction>())
                            }
                        }
                    }

                    is ArticleData.Available -> {
                        Log.i("GlanceButtonWidget", "Widget $id ArticleData.Available")
                        MyActualContent(
                            context = context,
                            articleType = articleType,
                            articleData = articleData,
                            regularFontSize,
                            smallFontSize,
                            chosenBrowser
                        )

                    }

                    is ArticleData.Unavailable -> {
                        Log.i("GlanceButtonWidget", "Widget $id ArticleData.UnAvailable")
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = GlanceModifier.background(
                                day = GlanceTheme.colors.background.getColor(context),
                                night = GlanceTheme.colors.background.getColor(context)
                            ).fillMaxSize()
                        )
                        {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Data not available", style = TextStyle(
                                        fontSize = regularFontSize.sp,
                                        textAlign = TextAlign.Center,
                                        color = GlanceTheme.colors.onBackground
                                    ), modifier = GlanceModifier.padding(20.dp)
                                )
                                Button("Refresh", actionRunCallback<RefreshAction>())
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MyActualContent(
        context: Context,
        articleType: String,
        articleData: ArticleData.Available,
        regularFontSize: Int,
        smallFontSize: Int,
        chosenBrowser: String
    ) {
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


                        Column(
                            modifier = GlanceModifier.padding(horizontal = 8.dp)

                        ) {
                            Text(
                                text = "${article.rank}. ${article.title}",
                                modifier = GlanceModifier.clickable(
                                    block = {
                                        makeAClick(
                                            context,
                                            article.url, chosenBrowser
                                        )
                                    }
                                ),
                                style = TextStyle(
                                    fontSize = regularFontSize.sp,
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
                                            article.commentUrl, chosenBrowser
                                        )
                                    }
                                ),
                                style = TextStyle(
                                    fontSize = smallFontSize.sp,
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

fun makeAClick(context: Context, url: String, chosenBrowser: String) {
    var webIntent = Intent(context, WebViewActivity::class.java)
    if (chosenBrowser == "inapp") {
        webIntent = Intent(context, WebViewActivity::class.java)
        webIntent.putExtra(WebViewActivity.EXTRA_URL, url)
    } else {
        webIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
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
        WidgetWorker.enqueue(context = context, force = true)
    }
}
