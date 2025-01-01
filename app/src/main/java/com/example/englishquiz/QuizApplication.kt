package com.example.englishquiz

import android.app.Application
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

        QuestionLoadingScript.importQuestionsFromJson(this, database)
        val appTimeTracker = AppTimeTrackerObserver(preferenceManager)
        ProcessLifecycleOwner.get().lifecycle.addObserver(appTimeTracker)
    }
}
