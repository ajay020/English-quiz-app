package com.example.englishquiz.data

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
)

data class Level(
    var level: Int,
    val questions: List<Question>,
)
