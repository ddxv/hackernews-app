package com.thirdgate.hackernews

import ThemeManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        setAppTheme()
        super.onCreate(savedInstanceState)

        // Fetch the articles
        lifecycleScope.launch {
            ArticlesRepository.fetchArticles("top")
            ArticlesRepository.fetchArticles("best")
            ArticlesRepository.fetchArticles("new")
        }



        setContent {
            MyAppTheme {
                MyApp {
                    NewsScreen()
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
    fun NewsScreen() {
        val topArticles = ArticlesRepository.topArticles.value
        val bestArticles = ArticlesRepository.bestArticles.value
        val newArticles = ArticlesRepository.newArticles.value

        var topPage by remember { mutableStateOf(1) }
        var bestPage by remember { mutableStateOf(1) }
        var newPage by remember { mutableStateOf(1) }

        var selectedTab by remember { mutableStateOf(0) }

        var showMenu by remember { mutableStateOf(false) }

        var articleType: String

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "News")
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                // handle menu item click
                                showMenu = false
                            }) {
                                Text("Option 1")
                            }
                            DropdownMenuItem(onClick = {
                                // handle menu item click
                                showMenu = false
                            }) {
                                Text("Option 2")
                            }
                            // ... other menu items
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


    private fun setAppTheme() {
        when (ThemeManager.getThemePreference(this)) {
            "EarthTheme" -> setTheme(R.style.EarthTheme)
            "CyberpunkTheme" -> setTheme(R.style.CyberpunkTheme)
            "DarculaTheme" -> setTheme(R.style.DarculaTheme)
            "CreamTheme" -> setTheme(R.style.CreamTheme)
            "SolarizedGrayTheme" -> setTheme(R.style.SolarizedGrayTheme)
            else -> setTheme(R.style.AppTheme)  // Default theme
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        when (item.itemId) {
            R.id.action_theme_default -> {
                ThemeManager.setThemePreference("Default", this)
                recreate()
                return true
            }

            R.id.action_theme_cyberpunk -> {
                ThemeManager.setThemePreference("Cyberpunk", this)
                recreate()
                return true
            }

            // ... other themes
        }
        return super.onOptionsItemSelected(item)
    }


}