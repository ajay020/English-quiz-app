package com.example.englishquiz.data

data class Level(
    val level: Int,
    val questions: List<Quiz>,
)

data class Quiz(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
)
