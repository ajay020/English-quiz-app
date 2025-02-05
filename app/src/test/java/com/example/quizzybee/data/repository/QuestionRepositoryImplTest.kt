package com.example.quizzybee.data.repository

import com.example.quizzybee.data.Question
import com.example.quizzybee.data.database.QuestionDao
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class QuestionRepositoryImplTest {
    private lateinit var questionDao: QuestionDao
    private lateinit var questionRepository: QuestionRepository

    @Before
    fun setup() {
        questionDao = mockk()
        questionRepository = QuestionRepositoryImpl(questionDao)
    }

    @Test
    fun `getUnsolvedQuestions returns expected list`() =
        runTest {
            // Arrange
            val mockQuestions =
                listOf(
                    Question(
                        id = 1,
                        questionText = "What is 2 + 2?",
                        correctAnswer = "4",
                        options = listOf("3", "4", "5"),
                        isSolved = false,
                    ),
                    Question(
                        id = 2,
                        questionText = "What is 3 + 3?",
                        correctAnswer = "6",
                        options = listOf("6", "7", "8"),
                        isSolved = false,
                    ),
                )
            coEvery { questionDao.getUnsolvedQuestions(2) } returns mockQuestions

            // Act
            val result = questionRepository.getUnsolvedQuestions(2)

            // Assert
            assertEquals(mockQuestions, result)
            coVerify { questionDao.getUnsolvedQuestions(2) }
        }

    @Test
    fun `markQuestionAsSolved marks question as solved`() =
        runTest {
            // Arrange
            val questionId = 1
//            coEvery { questionDao.markQuestionAsSolved(questionId) } returns Unit
            coJustRun { questionDao.markQuestionAsSolved(questionId) }

            // Act
            questionRepository.markQuestionAsSolved(questionId)

            // Assert
            coVerify { questionDao.markQuestionAsSolved(questionId) }
        }

    @Test
    fun `getAllQuestions returns expected list`() =
        runTest {
            // Arrange
            val mockQuestions =
                listOf(
                    Question(
                        id = 1,
                        questionText = "Sample Question 1",
                        correctAnswer = "Answer 1",
                        options = listOf("Option 1", "Option 2"),
                        isSolved = true,
                    ),
                )
            coEvery { questionDao.getAllQuestions() } returns mockQuestions

            // Act
            val result = questionRepository.getAllQuestions()

            // Assert
            assertEquals(mockQuestions, result)
            coVerify { questionDao.getAllQuestions() }
        }

    @Test
    fun `insertQuestion inserts a new question`() =
        runTest {
            val question =
                Question(
                    id = 1,
                    questionText = "What is the capital of France?",
                    options = listOf("Paris", "London", "Berlin", "Madrid"),
                    correctAnswer = "Paris",
                    isSolved = false,
                )

            coEvery { questionDao.insertQuestion(question) } returns Unit
            questionRepository.insertQuestion(question)
            coVerify { questionDao.insertQuestion(question) }
        }

    @Test
    fun `insertQuestionInBulk inserts a list of questions`() =
        runTest {
            val questions =
                listOf(
                    Question(
                        id = 1,
                        questionText = "What is the capital of France?",
                        options = listOf("Paris", "London", "Berlin", "Madrid"),
                        correctAnswer = "Paris",
                        isSolved = false,
                    ),
                )

            coEvery { questionDao.insertQuestionsInBulk(questions) } returns Unit

            questionRepository.insertQuestionInBulk(questions)

            coVerify { questionDao.insertQuestionsInBulk(questions) }
        }
}
