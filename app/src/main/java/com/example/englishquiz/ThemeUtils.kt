package com.example.englishquiz

object ThemeUtils {
    fun getSelectedThemeResourceId(theme: String): Int =
        when (theme) {
            "CLASSIC" -> R.style.AppTheme_classic
            "DARK" -> R.style.AppTheme_dark
            "NATURE" -> R.style.AppTheme_nature
            "OCEAN" -> R.style.AppTheme_ocean
            else -> R.style.AppTheme
        }
}
