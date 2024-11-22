package com.example.englishquiz

import android.content.Context

class PreferenceManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        val SELECTED_THEME_KEY = "selected_theme"
    }

    fun getSelectedThemeFromPreferences(): Theme {
        val themeName = sharedPreferences.getString(SELECTED_THEME_KEY, Theme.CLASSIC.name)
        return Theme.valueOf(themeName ?: Theme.CLASSIC.name)
    }

    fun saveSelectedThemeToPreferences(theme: Theme) {
        sharedPreferences.edit().putString(SELECTED_THEME_KEY, theme.name).apply()
    }

    // Add other preference-related functions here
}
