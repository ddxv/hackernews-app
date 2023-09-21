package com.thirdgate.hackernews

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArticleList(
    articles: List<ArticleData.ArticleInfo>,
    fontSize: String,
    browserPreference: String,
    onEndOfListReached: () -> Unit
) {
    val context = LocalContext.current
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableIntStateOf(15) }
    val lazyListState = rememberLazyListState()

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(Modifier.pullRefresh(state)) {
        LazyColumn(state = lazyListState) {
            items(articles) { article ->
                val url = article.url as? String ?: ""
                val commentUrl = article.commentUrl as? String ?: ""
                val browserIntent: Intent
                val commentIntent: Intent
                if (browserPreference == "inapp") {
                    browserIntent = Intent(context, WebViewActivity::class.java)
                    browserIntent.putExtra(WebViewActivity.EXTRA_URL, url)
                    commentIntent = Intent(context, WebViewActivity::class.java)
                    commentIntent.putExtra(WebViewActivity.EXTRA_URL, commentUrl)
                } else {
                    commentIntent = Intent(Intent.ACTION_VIEW, Uri.parse(commentUrl))
                    browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }
                // Default Browser
                ArticleView(
                    article = article,
                    onTitleClick = {
                        context.startActivity(browserIntent)
                    },
                    onCommentClick = {
                        context.startActivity(commentIntent)
                    }
                )
            }
        }
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))

    }
    // Check if the end of the list has been reached
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .map { it.lastOrNull() }
            .collectLatest { lastVisibleItem ->
                if (lastVisibleItem != null && lastVisibleItem.index == articles.size - 1) {
                    onEndOfListReached()
                }
            }
    }
}

@Composable
fun ArticleView(
    article: ArticleData.ArticleInfo,
    onTitleClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val rank = article.rank.toString().replace(".0", "")
    val title = article.title ?: ""
    val domain = article.domain as? String ?: ""
    val score = article.score.toString().replace(".0", "")
    val descendants = article.descendants.toString().replace(".0", "")
    val by = article.by as? String ?: ""
    val relativeTime = article.relativeTime as? String ?: ""

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .clickable(onClick = onTitleClick)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colors.onBackground
                        )
                    ) {
                        append("$rank. $title")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface
                        )
                    ) {
                        append(" ($domain)")
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .clickable(onClick = onCommentClick)
        ) {
            Text(
                text = "$score points by: $by $relativeTime | $descendants comments",
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

