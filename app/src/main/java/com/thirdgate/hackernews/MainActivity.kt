package com.thirdgate.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.thirdgate.hackernews.ui.theme.MyAppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Fetch the articles
        lifecycleScope.launch {
            ArticlesRepository.fetchArticles("top")
            ArticlesRepository.fetchArticles("best")
            ArticlesRepository.fetchArticles("new")
        }



        setContent {
            var currentTheme by remember { mutableStateOf("Default") }
            MyAppTheme(theme = currentTheme) {
                MyApp {
                    NewsScreen(currentTheme, onThemeChanged = { currentTheme = it })
                }
            }
        }
    }

    @Composable
    fun MyApp(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface {
                content()
            }
        }
    }

    @Composable
    fun NewsScreen(currentTheme: String, onThemeChanged: (String) -> Unit) {
        val topArticles = ArticlesRepository.topArticles.value
        val bestArticles = ArticlesRepository.bestArticles.value
        val newArticles = ArticlesRepository.newArticles.value

        var topPage by remember { mutableStateOf(1) }
        var bestPage by remember { mutableStateOf(1) }
        var newPage by remember { mutableStateOf(1) }

        var selectedTab by remember { mutableStateOf(0) }

        var showMenu by remember { mutableStateOf(false) }

        var articleType: String

        val themes = listOf(
            getString(R.string.default_theme),
            getString(R.string.cyberpunk_dark),
            getString(R.string.cyberpunk_light),
            getString(R.string.darcula),
            getString(R.string.lavender_light),
            getString(R.string.lavender_dark),
            getString(R.string.crystal_blue),
            getString(R.string.solarized_light),
            getString(R.string.solarized_dark),
            getString(R.string.hacker_news_orange_light),
            getString(R.string.hacker_news_orange_dark)
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "News")
                    },
                    actions = {
                        //var showMenu by remember1 { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            themes.forEach { theme ->
                                DropdownMenuItem(onClick = {
                                    onThemeChanged(theme)
                                    showMenu = false
                                }) {
                                    Text(theme)
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Top") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text("Best") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("New") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                when (selectedTab) {
                    0 -> {
                        articleType = "top"
                        when (topArticles) {
                            is ArticleData.Loading -> {
                                // Show a loading spinner
                            }

                            is ArticleData.Unavailable -> {
                                // Show an error message
                            }

                            is ArticleData.Available -> {
                                ArticleList(
                                    articles = getArticles(topArticles, articleType),
                                    onEndOfListReached = {
                                        // Fetch more 'top' articles
                                        lifecycleScope.launch {
                                            topPage++
                                            ArticlesRepository.fetchArticles(
                                                articleType,
                                                page = topPage
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        articleType = "best"
                        when (bestArticles) {
                            is ArticleData.Loading -> {
                                // Show a loading spinner
                            }

                            is ArticleData.Unavailable -> {
                                // Show an error message
                            }

                            is ArticleData.Available -> {
                                ArticleList(
                                    articles = getArticles(bestArticles, articleType),
                                    onEndOfListReached = {
                                        // Fetch more 'best' articles
                                        lifecycleScope.launch {
                                            bestPage++
                                            ArticlesRepository.fetchArticles(
                                                articleType,
                                                page = bestPage
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    2 -> {
                        articleType = "new"
                        when (newArticles) {
                            is ArticleData.Loading -> {
                                // Show a loading spinner
                            }

                            is ArticleData.Unavailable -> {
                                // Show an error message
                            }

                            is ArticleData.Available -> {
                                ArticleList(
                                    articles = getArticles(newArticles, articleType),
                                    onEndOfListReached = {
                                        // Fetch more 'new' articles
                                        lifecycleScope.launch {
                                            newPage++
                                            ArticlesRepository.fetchArticles(
                                                articleType,
                                                page = newPage
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getArticles(articleData: ArticleData, type: String): List<ArticleData.ArticleInfo> {
        return when (articleData) {
            is ArticleData.Available -> articleData.articles[type] ?: emptyList()
            else -> emptyList()
        }
    }

}