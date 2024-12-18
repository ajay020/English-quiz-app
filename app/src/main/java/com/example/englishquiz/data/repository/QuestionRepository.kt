package com.example.englishquiz.data.repository

import com.example.englishquiz.data.database.Question

interface QuestionRepository {
    // Fetch unsolved questions
    suspend fun getUnsolvedQuestions(limit: Int): List<Question>

    // Mark a question as solved
    suspend fun markQuestionAsSolved(id: Int)

    // Fetch all questions
    suspend fun getAllQuestions(): List<Question>

    // Insert a new question
    suspend fun insertQuestion(question: Question)

    // Insert a new question
    suspend fun insertQuestionInBulk(questions: List<Question>)
}
