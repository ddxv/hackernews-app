package com.thirdgate.hackernews

import ApiService
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thirdgate.hackernews.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiService = ApiService()
    private val viewModel: SharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fetchArticles("top")
        viewModel.fetchArticles("best")
        viewModel.fetchArticles("new")



        setAppTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Make sure to set the content view first

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val bundle = when (item.itemId) {
                R.id.topNews -> bundleOf("articleType" to "top")
                R.id.latestNews -> bundleOf("articleType" to "new")
                R.id.bestNews -> bundleOf("articleType" to "best")
                // Add other cases as needed
                else -> null
            }
            if (bundle != null) {
                navController.navigate(R.id.NewsFragment, bundle)
                true
            } else {
                false
            }
        }

        setSupportActionBar(binding.toolbar)


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