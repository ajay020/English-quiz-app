package com.example.englishquiz.data.repository

import com.example.englishquiz.data.database.Question
import com.example.englishquiz.data.database.QuestionDao

class QuestionRepositoryImpl(
    private val questionDao: QuestionDao,
) : QuestionRepository {
    // Fetch unsolved questions
    override suspend fun getUnsolvedQuestions(limit: Int): List<Question> = questionDao.getUnsolvedQuestions(limit)

    // mark the question with id as solved
    override suspend fun markQuestionAsSolved(id: Int) {
        questionDao.markQuestionAsSolved(id)
    }

    override suspend fun getAllQuestions(): List<Question> = questionDao.getAllQuestions()

    // Insert a new question
    override suspend fun insertQuestion(question: Question) {
        questionDao.insertQuestion(question)
    }

    // Insert a new question
    override suspend fun insertQuestionInBulk(questions: List<Question>) {
        questionDao.insertQuestionsInBulk(questions)
    }
}
