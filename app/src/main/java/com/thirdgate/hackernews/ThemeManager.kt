package com.thirdgate.hackernews

import android.content.Context


object ThemeManager {
    private const val THEME_PREFS = "theme_prefs"
    private const val THEME_KEY = "theme_key"

    fun setThemePreference(theme: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(THEME_KEY, theme).apply()
    }

    fun getThemePreference(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(THEME_KEY, "AppTheme") ?: "AppTheme"
    }
}
