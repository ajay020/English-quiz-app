package com.example.quizzybee.viewmodel

import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzybee.data.Question
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.data.repository.QuestionRepository
import com.example.quizzybee.utils.constants.AppConstants
import com.example.quizzybee.utils.managers.StreakManager
import com.example.quizzybee.utils.managers.TimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

const val QUESTION_NUMBER_PER_QUIZ = 3

@HiltViewModel
class QuizViewModel
    @Inject
    constructor(
        private val preferenceManager: PreferenceManager,
        private val questionRepository: QuestionRepository,
        private val streakManager: StreakManager,
        private val timerManager: TimerManager,
    ) : ViewModel() {
        private val _currentQuestion = MutableLiveData<Question>()
        val currentQuestion: LiveData<Question> = _currentQuestion

        private val _coins = MutableLiveData(0)
        val coins: LiveData<Int> = _coins

        private val _currentLevel = MutableLiveData<Int>(0)
        val currentLevel: LiveData<Int> = _currentLevel

        private val _navigationEvent = MutableLiveData<NavigationEvent>()
        val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

        private val _timeLeft = MutableLiveData<Long>()
        val timeLeft: LiveData<Long> = _timeLeft

        private val _smoothProgress = MutableLiveData<Float>()
        val smoothProgress: LiveData<Float> = _smoothProgress

        private val _questionProgress = MutableLiveData<String>("")
        val questionProgress: LiveData<String> = _questionProgress

        private val _areAnswerButtonsEnabled = MutableLiveData(true)
        val areAnswerButtonsEnabled: LiveData<Boolean> = _areAnswerButtonsEnabled

        var questions: List<Question> = emptyList()
        var currentQuestionIndex = 0
        private var lastCoinValue = 0
        val solvedQuestions = mutableListOf<Question>()

        init {
            initializeQuizState()
        }

        private fun initializeQuizState() {
            _currentLevel.value = preferenceManager.getCurrentLevel()
            _coins.value = preferenceManager.getCoins()
            generateLevel()
            _timeLeft.value = timerManager.getTimeLeft()
        }

        fun generateLevel() {
            // clear solved question list
            solvedQuestions.clear()
            currentQuestionIndex = 0

            viewModelScope.launch {
                try {
                    questions = questionRepository.getUnsolvedQuestions(QUESTION_NUMBER_PER_QUIZ)
                    solvedQuestions.addAll(questions)
                    _questionProgress.value = "${currentQuestionIndex + 1} / ${questions.size}"
                    displayCurrentQuestion()
                } catch (e: Exception) {
                    Log.e("DATA", "Error loading questions: ${e.message}")
                }
            }
            _currentLevel.value = preferenceManager.getCurrentLevel()
        }

        private fun displayCurrentQuestion() {
            if (questions.isNotEmpty()) {
                _currentQuestion.value = questions[currentQuestionIndex]
                startQuestionTimer() // Start timer for new question
            } else {
                _navigationEvent.value = NavigationEvent.NavigateToResult
            }
        }

        fun checkAnswer(selectedAnswer: String) {
            stopTimer() // Stop timer when answer is selected
            _currentQuestion.value?.let { question ->
                if (selectedAnswer == question.correctAnswer) {
                    addCoins(2)
                    // mark current question as correct and add to solved list
                    solvedQuestions[currentQuestionIndex] =
                        solvedQuestions[currentQuestionIndex].copy(isSolved = true)
                }
            }
        }

        fun setCurrentLevel(level: Int) {
            _currentLevel.value = level
        }

        fun setCurrentQuestion(question: Question) {
            _currentQuestion.value = question
        }

        fun useHint(optionButtons: List<Button>) {
            val hintCost = AppConstants.HINT_COST

            _coins.value?.let { currentCoins ->
                if (currentCoins >= hintCost) {
                    val currentQuestion = currentQuestion.value ?: return
                    val incorrectOptions =
                        optionButtons.filter {
                            it.text != currentQuestion.correctAnswer && it.visibility == View.VISIBLE
                        }

                    val optionsToHideCount = ceil(incorrectOptions.size / 2.0).toInt()

                    // Ensure at least 1 incorrect options remain before applying a hint
                    if (optionsToHideCount >= 1) {
                        val optionsToHide =
                            incorrectOptions
                                .shuffled()
                                .take(optionsToHideCount)

                        // Hide the selected options
                        optionsToHide.forEach { it.visibility = View.INVISIBLE }

                        // Deduct coins
                        deductCoins(hintCost)
                    }
                } else {
                    _navigationEvent.value = NavigationEvent.ShowToast("Not enough coins for a hint!")
                }
            }
        }

        fun onNextQuestion() {
            stopTimer() // Stop timer before moving to next question
            currentQuestionIndex++
            _questionProgress.value = "${currentQuestionIndex + 1} / ${questions.size}"

            if (currentQuestionIndex >= questions.size) {
                handleQuizCompletion()
            } else {
                displayCurrentQuestion()
            }
        }

        private fun handleQuizCompletion() {
            currentQuestionIndex = 0
            _questionProgress.value = "0 / ${questions.size}"
            val currentLevel = currentLevel.value ?: 0

            saveSolvedQuestions()

            // Mark the current day as completed
            streakManager.markDayAsCompleted()
            val isStreakCompleted = streakManager.isStreakCompleted()

            val correctQuestions = solvedQuestions.count { it.isSolved }
            val totalQuestions = solvedQuestions.size

            if (questions.isEmpty()) {
                _navigationEvent.value = NavigationEvent.NavigateToResult
            } else {
                _navigationEvent.value =
                    NavigationEvent.ShowQuizComplete(
                        level = currentLevel,
                        correctQuestions = correctQuestions,
                        totalQuestions = totalQuestions,
                        isStreakCompleted = isStreakCompleted,
                    )
            }
        }

        fun saveSolvedQuestions() {
            viewModelScope.launch {
                solvedQuestions.forEach { question ->
                    if (question.isSolved) {
                        questionRepository.markQuestionAsSolved(question.id)
                    }
                }
            }
        }

        private fun addTime(extraTimeInMillis: Long) {
            timerManager.addTimeDuration(extraTimeInMillis)
        }

        private fun startQuestionTimer() {
            timerManager.startTimer(
                onTick = { seconds, progress ->
                    _timeLeft.value = seconds
                    _smoothProgress.value = progress
                },
                onFinish = {
                    _navigationEvent.value =
                        NavigationEvent.ShowQuizComplete(
                            level = _currentLevel.value ?: 0,
                            correctQuestions = solvedQuestions.count { it.isSolved },
                            totalQuestions = solvedQuestions.size,
                            isStreakCompleted = streakManager.isStreakCompleted(),
                        )
                    disableAnswerButtons()
                },
            )
        }

        private fun stopTimer() {
            timerManager.stopTimer()
        }

        fun buyMoreTime() {
            val extraTime = 10000L
            val timeCost = AppConstants.TIMER_BOOST_COST

            _coins.value?.let { currentCoins ->
                if (currentCoins >= timeCost) {
                    deductCoins(timeCost)
                    addTime(extraTime)
                    enableAnswerButtons()
                } else {
                    _navigationEvent.value = NavigationEvent.ShowToast("Not enough coins!")
                }
            }
        }

        fun restartLevel() {
            currentQuestionIndex = 0
            _questionProgress.value = "0 / ${questions.size}"
            solvedQuestions.clear()
            solvedQuestions.addAll(questions)
            startQuestionTimer()
            enableAnswerButtons()
            displayCurrentQuestion()
        }

        private fun disableAnswerButtons() {
            _areAnswerButtonsEnabled.value = false
        }

        private fun enableAnswerButtons() {
            _areAnswerButtonsEnabled.value = true
        }

        // Clean up timer when ViewModel is cleared
        override fun onCleared() {
            super.onCleared()
            stopTimer()
        }

        fun deductCoins(amount: Int = 20) {
            _coins.value?.let { currentCoins ->
                if (currentCoins >= amount) {
                    val newCoins = currentCoins - amount
                    preferenceManager.saveCoins(newCoins)
                    _coins.value = newCoins
                } else {
                    _navigationEvent.value = NavigationEvent.ShowToast("Not enough coins!")
                }
            }
        }

        fun addCoins(amount: Int) {
            _coins.value?.let { currentCoins ->
                lastCoinValue = currentCoins
                val newCoins = currentCoins + amount
                preferenceManager.saveCoins(newCoins)
                _coins.value = newCoins
            }
        }

        fun getLastCoinValue(): Int = lastCoinValue

        fun pauseGame() {
            // Stop the timer and save the remaining time
            timerManager.stopTimer()
            _areAnswerButtonsEnabled.value = false // Disable answer buttons during pause
        }

        fun resumeGame() {
            // Resume the timer with the remaining time
            val remainingTime = timerManager.getTimeLeft()
            timerManager.startTimer(
                duration = remainingTime,
                onTick = { seconds, progress ->
                    _timeLeft.value = seconds
                    // Calculate smooth progress: (remainingTime / totalTime) * maxProgress
                    _smoothProgress.value = progress.toFloat()
                },
                onFinish = {
                    disableAnswerButtons()
                },
            )
            _areAnswerButtonsEnabled.value = true // Enable answer buttons when resuming
        }

        sealed class NavigationEvent {
            data class ShowToast(
                val message: String,
            ) : NavigationEvent()

            data class ShowQuizComplete(
                val level: Int,
                val correctQuestions: Int,
                val totalQuestions: Int,
                val isStreakCompleted: Boolean,
            ) : NavigationEvent()

            data object NavigateToResult : NavigationEvent()
        }
    }
