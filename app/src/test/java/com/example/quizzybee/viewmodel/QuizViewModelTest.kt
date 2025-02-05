package com.example.quizzybee.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.quizzybee.data.Question
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.data.repository.QuestionRepository
import com.example.quizzybee.utils.managers.StreakManager
import com.example.quizzybee.utils.managers.TimerManager
import com.example.quizzybee.views.StreakTrackerView
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuizViewModelTest {
    // Use this rule to execute LiveData updates instantly (synchronously)
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Use this dispatcher to control the execution of coroutines in tests
    private val testDispatcher = StandardTestDispatcher()

    // mocks for dependencies
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var questionRepository: QuestionRepository
    private lateinit var timerManager: TimerManager
    private lateinit var streakManager: StreakManager
    private lateinit var trackerView: StreakTrackerView

    // class under test
    private lateinit var viewModel: QuizViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // Set Main dispatcher to test dispatcher
        Dispatchers.setMain(testDispatcher)

        preferenceManager = mockk(relaxed = true)
        questionRepository = mockk(relaxed = true)
        timerManager = mockk(relaxed = true)
        trackerView = mockk(relaxed = true)
        streakManager = mockk(relaxed = true)

        every { preferenceManager.getCurrentLevel() } returns 1
        every { preferenceManager.getCoins() } returns 10
        every { timerManager.getTimeLeft() } returns 60_000L

        viewModel = QuizViewModel(preferenceManager, questionRepository, streakManager, timerManager)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset Main dispatcher to default
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeQuizState sets initial state correctly`() =
        runTest {
            // Verify initial values
            assertEquals(1, viewModel.currentLevel.value)
            assertEquals(10, viewModel.coins.value)
            assertEquals(60_000L, viewModel.timeLeft.value)
        }

    @Test
    fun `generateLevel fetches questions and updates state`() =
        runTest {
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

            coEvery { questionRepository.getUnsolvedQuestions(any()) } returns mockQuestions

            viewModel.generateLevel()

            // Advance the coroutine
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                2,
                viewModel.questionProgress.value
                    ?.split(" / ")
                    ?.last()
                    ?.toInt(),
            )
            assertEquals(mockQuestions[0], viewModel.currentQuestion.value)

            coVerify { questionRepository.getUnsolvedQuestions(3) }
        }

    @Test
    fun `checkAnswer adds coins for correct answer`() {
        // Set up a current question
        val question =
            Question(
                id = 1,
                questionText = "Capital of France?",
                correctAnswer = "Paris",
                options = listOf("Paris", "London"),
            )
        viewModel.setCurrentQuestion(question)

        // Simulate answering correctly
        viewModel.checkAnswer("Paris")

        assertEquals(12, viewModel.coins.value) // 10 initial + 2
    }

    @Test
    fun `onNextQuestion navigates to results if all questions are answered`() {
        val mockQuestions =
            listOf(
                Question(id = 1, questionText = "Question 1?", correctAnswer = "Answer 1", options = emptyList()),
                Question(id = 2, questionText = "Question 2?", correctAnswer = "Answer 2", options = emptyList()),
            )

        viewModel.setCurrentLevel(1)
        viewModel.questions = mockQuestions
        viewModel.currentQuestionIndex = 1 // Last question

        viewModel.onNextQuestion()

        // Advance the coroutine
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(QuizViewModel.NavigationEvent.NavigateToResult, viewModel.navigationEvent.value)
    }

    @Test
    fun `restartLevel resets state and starts new level`() {
        viewModel.restartLevel()

        assertEquals(0, viewModel.currentQuestionIndex)
        assertEquals(
            0,
            viewModel.questionProgress.value
                ?.split(" / ")
                ?.first()
                ?.toInt(),
        )
        verify { timerManager.startTimer(any(), any(), any()) }
    }

    @Test
    fun `buyMoreTime adds time when coins are sufficient`() {
        viewModel.buyMoreTime()

        assertEquals(5, viewModel.coins.value) // 10 initial - 5 for time
        verify { timerManager.addTimeDuration(10_000L) }
    }

    @Test
    fun `pauseGame disables answer buttons and stops timer`() {
        viewModel.pauseGame()

        assertEquals(false, viewModel.areAnswerButtonsEnabled.value)
        verify { timerManager.stopTimer() }
    }

    @Test
    fun `resumeGame enables answer buttons and resumes timer`() {
        every { timerManager.getTimeLeft() } returns 30_000L

        viewModel.resumeGame()

        assertEquals(true, viewModel.areAnswerButtonsEnabled.value)
        verify { timerManager.startTimer(any(), any(), any()) }
    }
}
