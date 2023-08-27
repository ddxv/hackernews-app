object ThemeManager {

    private const val THEME_PREFERENCES = "theme_preferences"
    private const val THEME_KEY = "theme_key"

    fun getThemePreference(context: Context): String {
        val sharedPreferences =
            context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(THEME_KEY, "Default") ?: "Default"
    }

    fun setThemePreference(theme: String, context: Context) {
        val editor = context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putString(THEME_KEY, theme)
        editor.apply()
    }

    fun applyTheme(theme: String, activity: Activity) {
        when (theme) {
            "Default" -> activity.setTheme(R.style.Theme_MyApp)
            "Cyberpunk" -> activity.setTheme(R.style.Theme_MyApp_Cyberpunk)
            // ... other themes
        }
    }
}