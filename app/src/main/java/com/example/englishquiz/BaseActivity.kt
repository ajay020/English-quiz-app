package com.example.englishquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme
        preferenceManager = PreferenceManager(this)
        val selectedTheme = preferenceManager.getSelectedThemeFromPreferences()
        setTheme(ThemeUtils.getSelectedThemeResourceId(selectedTheme.name))
        super.onCreate(savedInstanceState)
    }
}
