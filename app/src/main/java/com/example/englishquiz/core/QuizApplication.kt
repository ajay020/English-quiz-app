package com.example.englishquiz.core

import android.app.Application
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.data.repository.QuestionRepositoryImpl
import com.example.englishquiz.utils.managers.SoundManager

class QuizApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    lateinit var questionRepository: QuestionRepositoryImpl
    lateinit var preferenceManager: PreferenceManager
    lateinit var soundManager: SoundManager

    override fun onCreate() {
        super.onCreate()
        soundManager = SoundManager(this)
        preferenceManager = PreferenceManager(this)
        questionRepository = QuestionRepositoryImpl(database.questionDao())
    }
}
