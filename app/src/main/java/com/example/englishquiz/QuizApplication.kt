package com.example.englishquiz

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.AppTimeTrackerObserver
import com.example.englishquiz.utils.QuestionLoadingScript
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class QuizApplication : Application() {
    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate() {
        super.onCreate()

        val preferenceManager = PreferenceManager(this)
        if (preferenceManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        QuestionLoadingScript.importQuestionsFromJson(this, database)
        val appTimeTracker = AppTimeTrackerObserver(preferenceManager)
        ProcessLifecycleOwner.get().lifecycle.addObserver(appTimeTracker)
    }
}
