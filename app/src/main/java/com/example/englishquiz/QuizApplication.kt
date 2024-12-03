package com.example.englishquiz

import android.app.Application

class QuizApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
