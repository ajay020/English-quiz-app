package com.example.englishquiz.data

import android.app.Application
import android.content.Context

interface PreferenceStorage {
    fun getInt(
        key: String,
        defaultValue: Int = 0,
    ): Int

    fun putInt(
        key: String,
        value: Int,
    )

    fun getStringSet(key: String): Set<String>

    fun putStringSet(
        key: String,
        value: Set<String>,
    )
}

class SharedPreferenceStorage(
    private val application: Application,
) : PreferenceStorage {
    private val prefs = application.getSharedPreferences("QuizApp", Context.MODE_PRIVATE)

    override fun getInt(
        key: String,
        defaultValue: Int,
    ) = prefs.getInt(key, defaultValue)

    override fun putInt(
        key: String,
        value: Int,
    ) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun getStringSet(key: String) = prefs.getStringSet(key, emptySet()) ?: emptySet()

    override fun putStringSet(
        key: String,
        value: Set<String>,
    ) {
        prefs.edit().putStringSet(key, value).apply()
    }
}
