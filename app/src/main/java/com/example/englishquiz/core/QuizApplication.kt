package com.example.englishquiz.core

import android.app.Application
import com.example.englishquiz.data.database.AppDatabase

class QuizApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
