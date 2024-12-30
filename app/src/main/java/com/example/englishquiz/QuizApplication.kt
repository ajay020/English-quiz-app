package com.example.englishquiz

import android.app.Application
import android.util.Log
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.utils.QuestionLoadingScript
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class QuizApplication : Application() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        QuestionLoadingScript.importQuestionsFromJson(this, database)
        Log.d("QuizApplication", "Questions loaded in non-test environment!")
    }
}
