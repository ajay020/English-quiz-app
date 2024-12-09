package com.example.englishquiz.utils

import com.example.englishquiz.R

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
