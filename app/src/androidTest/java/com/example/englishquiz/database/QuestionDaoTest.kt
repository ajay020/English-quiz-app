package com.example.englishquiz.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.database.QuestionDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class QuestionDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase
    private lateinit var questionDao: QuestionDao

    @Before
    fun setup() {
        hiltRule.inject()
        questionDao = database.questionDao()
    }

    @Test
    fun testInsertQuestion() =
        runBlocking {
            val question =
                Question(
                    id = 0,
                    questionText = "What is the capital of France?",
                    options = listOf("Paris", "London", "Berlin", "Madrid"),
                    correctAnswer = "Paris",
                    isSolved = false,
                )
            questionDao.insertQuestion(question)
            val retrievedQuestion = questionDao.getAllQuestions()

            // Assert
            assertNotNull(retrievedQuestion)
            assert(1 == retrievedQuestion.size)
        }

    @After
    fun tearDown() {
        database.close()
    }
}
