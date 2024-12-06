package com.example.englishquiz

import com.example.englishquiz.data.Question
import com.example.englishquiz.data.QuestionDao

class QuestionRepository(
    private val questionDao: QuestionDao,
) {
    // Fetch unsolved questions
    suspend fun getUnsolvedQuestions(limit: Int): List<Question> = questionDao.getUnsolvedQuestions(limit)

    //
    suspend fun markQuestionAsSolved(id: Int) {
        questionDao.markQuestionAsSolved(id)
    }

    suspend fun getAllQuestions(): List<Question> = questionDao.getAllQuestions()

    // Insert a new question
    suspend fun insertQuestion(question: Question) {
        questionDao.insertQuestion(question)
    }

    // Insert a new question
    suspend fun insertQuestionInBulk(questions: List<Question>) {
        questionDao.insertQuestionsInBulk(questions)
    }
}
