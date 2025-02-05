package com.example.quizzybee.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quizzybee.data.Question
import kotlinx.coroutines.flow.Flow

// Data Access Object for Questions
@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): Question?

    @Query("UPDATE questions SET isSolved = 1 WHERE id = :id")
    suspend fun markQuestionAsSolved(id: Int)

    @Query("SELECT * FROM questions WHERE isSolved = 0 LIMIT :limit")
    suspend fun getUnsolvedQuestions(limit: Int): List<Question>

    // method to find question by exact text
    @Query("SELECT * FROM questions WHERE questionText = :questionText LIMIT 1")
    suspend fun getQuestionByText(questionText: String): Question?

    // Method to update existing question
    @Update
    fun update(question: Question)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionsInBulk(questions: List<Question>)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionCount(): Flow<Int>

    @Query("UPDATE questions SET isSolved = 0")
    suspend fun resetAllQuestions()
}
