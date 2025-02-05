package com.example.quizzybee.utils.managers

import androidx.appcompat.app.AppCompatDelegate
import com.example.quizzybee.data.preferences.PreferenceManager
import javax.inject.Inject

class ThemeManager
    @Inject
    constructor(
        private val preferenceManager: PreferenceManager,
    ) {
        fun applyTheme() {
            val isDarkModeEnabled = preferenceManager.isDarkModeEnabled()
            val mode =
                if (isDarkModeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        fun setDarkMode(isDark: Boolean) {
            preferenceManager.saveThemePreference(isDark)
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun isDarkMode(): Boolean = preferenceManager.isDarkModeEnabled()
    }
