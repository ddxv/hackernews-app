package com.thirdgate.hackernews

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                ArticleView(
                    article = article,
                    onTitleClick = {
                        val url = article.url as? String ?: ""
                        val context = context
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(browserIntent)
                    },
                    onCommentClick = {
                        val commentUrl = article.commentUrl as? String ?: ""
                        val context = context
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(commentUrl))
                        context.startActivity(browserIntent)
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
    val rank = article.rank.toString().replace(".0", "") ?: ""
    val title = article.title as? String ?: ""
    val domain = article.domain as? String ?: ""
    val score = article.score.toString()?.replace(".0", "") ?: ""
    val descendants = article.descendants.toString().replace(".0", "") ?: ""
    val by = article.by as? String ?: ""

    Column {
        Row(
            modifier = Modifier
                .clickable(onClick = onTitleClick)
                .padding(8.dp)
        ) {
            Text(
                text = "$rank. $title ($domain)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Row(
            modifier = Modifier
                .clickable(onClick = onCommentClick)
                .padding(8.dp)
        ) {
            Text(
                text = "$score points by: $by | $descendants comments",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

