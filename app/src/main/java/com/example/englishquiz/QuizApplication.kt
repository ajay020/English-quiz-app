package com.example.englishquiz

import android.app.Application
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.utils.QuestionLoadingScript
import dagger.hilt.android.HiltAndroidApp
import nl.dionsegijn.konfetti.core.BuildConfig
import javax.inject.Inject

@HiltAndroidApp
class QuizApplication : Application() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // Load JSON only in non-test environments
            QuestionLoadingScript.importQuestionsFromJson(this, database)
            println("Questions loaded in non-test environment!")
        }

        if (BuildConfig.DEBUG) {
            println("Questions loaded in debug environment!")
        } else {
            println("Questions loaded in release environment!")
        }
    }
}
