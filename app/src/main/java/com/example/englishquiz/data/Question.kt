package com.example.englishquiz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val isSolved: Boolean = false,
)
