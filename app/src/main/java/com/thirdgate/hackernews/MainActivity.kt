package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.thirdgate.hackernews.ArticlesRepository.fetchTheme
import com.thirdgate.hackernews.ui.theme.MyAppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Hide the status bar
        actionBar?.hide()

        var readTheme: String = "Default"

        val context: Context = this

        // Fetch the articles
        lifecycleScope.launch {
            ArticlesRepository.fetchArticles("top")
            ArticlesRepository.fetchArticles("best")
            ArticlesRepository.fetchArticles("new")
        }


        setContent {
            var currentTheme by remember { mutableStateOf("Default") }
            LaunchedEffect(key1 = Unit) {
                currentTheme = fetchTheme(context)
            }
            MyAppTheme(theme = currentTheme) {
                MyApp {
                    NewsScreen(onThemeChanged = { theme ->
                        currentTheme = theme
                        lifecycleScope.launch {
                            ArticlesRepository.writeTheme(context, theme)
                        }
                    })
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
    fun NewsScreen(onThemeChanged: (String) -> Unit) {
        val topArticles = ArticlesRepository.topArticles.value
        val bestArticles = ArticlesRepository.bestArticles.value
        val newArticles = ArticlesRepository.newArticles.value

        var topPage by remember { mutableStateOf(1) }
        var bestPage by remember { mutableStateOf(1) }
        var newPage by remember { mutableStateOf(1) }

        var selectedTab by remember { mutableStateOf(0) }

        var showMenu by remember { mutableStateOf(false) }

        val context = LocalContext.current

        val themes = listOf(
            getString(R.string.hacker_news_orange_light),
            getString(R.string.hacker_news_orange_dark),
            getString(R.string.darcula),
            getString(R.string.cyberpunk_light),
            getString(R.string.cyberpunk_dark),
            getString(R.string.lavender_light),
            getString(R.string.lavender_dark),
            getString(R.string.crystal_blue),
            getString(R.string.solarized_light),
            getString(R.string.solarized_dark),
        )

        val articleType = when (selectedTab) {
            0 -> "top"
            1 -> "best"
            2 -> "new"
            else -> "top"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Hacker News: " + articleType.replaceFirstChar { it.uppercase() })
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            themes.forEach { theme ->
                                DropdownMenuItem(
                                    onClick = {
                                        onThemeChanged(theme)
                                        lifecycleScope.launch {
                                            ArticlesRepository.writeTheme(context, theme)
                                        }
                                        showMenu = false
                                    },
                                    modifier = Modifier.background(color = MaterialTheme.colors.background)
                                ) {
                                    Text(theme, color = MaterialTheme.colors.onBackground)
                                }
                            }
                            DropdownMenuItem(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            context,
                                            AboutActivity::class.java
                                        )
                                    )
                                },
                                modifier = Modifier.background(color = MaterialTheme.colors.background)
                            ) { Text("About App", color = MaterialTheme.colors.onBackground) }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                ) {
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
                        when (topArticles) {
                            is ArticleData.Loading -> {
                                Log.i("MainActivity", "Content Loading")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top, // or Arrangement.Center, Arrangement.Bottom
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.padding(40.dp))
                                        Text("Data loading... pull to refresh")
                                    }
                                }
                            }

                            is ArticleData.Unavailable -> {
                                // Show an error message
                                Log.i("MainActivity", "Content UnAvailable")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                )
                                {
                                    Text("Data unavailable...")
                                }
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
                        when (bestArticles) {
                            is ArticleData.Loading -> {

                                Log.i("MainActivity", "Content Loading")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top, // or Arrangement.Center, Arrangement.Bottom
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.padding(40.dp))
                                        Text("Data loading... pull to refresh")
                                    }
                                }
                            }

                            is ArticleData.Unavailable -> {
                                // Show an error message
                                Log.i("MainActivity", "Content UnAvailable")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                )
                                {
                                    Text("Data unavailable...")
                                }
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
                        when (newArticles) {
                            is ArticleData.Loading -> {
                                Log.i("MainActivity", "Content Loading")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top, // or Arrangement.Center, Arrangement.Bottom
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.padding(40.dp))
                                        Text("Data loading... pull to refresh")
                                    }
                                }
                            }

                            is ArticleData.Unavailable -> {
                                Log.i("MainActivity", "Content UnAvailable")
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                )
                                {
                                    Text("Data unavailable...")
                                }
                            }

                            is ArticleData.Available -> {
                                Log.i("MainActivity", "Content Aavailable")
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

    private fun getArticles(articleData: ArticleData, type: String): List<ArticleData.ArticleInfo> {
        return when (articleData) {
            is ArticleData.Available -> articleData.articles[type] ?: emptyList()
            else -> emptyList()
        }
    }

}