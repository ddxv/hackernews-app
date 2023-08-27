package com.thirdgate.hackernews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
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
            MyApp {
                NewsScreen()
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

        var selectedTab by remember { mutableStateOf(0) }

        var showMenu by remember { mutableStateOf(false) }

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
                    0 -> ArticleList(articles = getArticles(topArticles, "top"))
                    1 -> ArticleList(articles = getArticles(bestArticles, "best"))
                    2 -> ArticleList(articles = getArticles(newArticles, "new"))
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

    @Composable
    fun ArticleList(articles: List<ArticleData.ArticleInfo>) {
        val context = LocalContext.current
        LazyColumn {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_theme_default -> {
                ThemeManager.setThemePreference("AppTheme", this)
                recreate()
                return true
            }

            R.id.action_theme_cream -> {
                ThemeManager.setThemePreference("CreamTheme", this)
                recreate()
                return true
            }

            R.id.action_theme_earth -> {
                ThemeManager.setThemePreference("EarthTheme", this)
                recreate()
                return true
            }

            R.id.action_theme_cyberpunk -> {
                ThemeManager.setThemePreference("CyberpunkTheme", this)
                recreate()
                return true
            }


            R.id.action_theme_darcula -> {
                ThemeManager.setThemePreference("DarculaTheme", this)
                recreate()
                return true
            }


            R.id.action_theme_solarized -> {
                ThemeManager.setThemePreference("SolarizedGrayTheme", this)
                recreate()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}