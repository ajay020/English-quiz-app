package com.example.englishquiz.viewmodel

import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.englishquiz.core.QuizApplication
import com.example.englishquiz.data.database.Question
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.data.repository.QuestionRepositoryImpl
import com.example.englishquiz.utils.managers.TimerManager
import com.example.englishquiz.views.StreakTrackerView
import kotlinx.coroutines.launch
import kotlin.math.ceil

class QuizViewModel(
    private val preferenceManager: PreferenceManager,
    private val questionRepository: QuestionRepositoryImpl,
    private val streakTrackerView: StreakTrackerView,
    private val timerManager: TimerManager,
) : ViewModel() {
    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _coins = MutableLiveData(200)
    val coins: LiveData<Int> = _coins

    private val _currentLevel = MutableLiveData<Int>(0)
    val currentLevel: LiveData<Int> = _currentLevel

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _questionProgress = MutableLiveData<String>("")
    val questionProgress: LiveData<String> = _questionProgress

    private val _showTimeUpDialog = MutableLiveData<Boolean>()
    val showTimeUpDialog: LiveData<Boolean> = _showTimeUpDialog

    private val _areAnswerButtonsEnabled = MutableLiveData(true)
    val areAnswerButtonsEnabled: LiveData<Boolean> = _areAnswerButtonsEnabled

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var lastCoinValue = 0

    init {
//        val questionDao = (application as QuizApplication).database.questionDao()
//        questionRepository = QuestionRepositoryImpl(questionDao)
        initializeQuizState()
    }

    private fun initializeQuizState() {
        _currentLevel.value = preferenceManager.getCurrentLevel()
        _coins.value = preferenceManager.getCoins()
        generateLevel()
        _timeLeft.value = timerManager.getTimeLeft()
    }

    fun generateLevel() {
        viewModelScope.launch {
            try {
                questions = questionRepository.getUnsolvedQuestions(3)
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
            }
        }
    }

    fun useHint(optionButtons: List<Button>) {
        val hintCost = 5

        _coins.value?.let { currentCoins ->
            if (currentCoins >= hintCost) {
                val currentQuestion = currentQuestion.value ?: return
                val incorrectOptions =
                    optionButtons.filter { it.text != currentQuestion.correctAnswer }

                // Calculate how many incorrect options to hide (round up for odd numbers)
                val optionsToHideCount = ceil(incorrectOptions.size / 2.0).toInt()

                if (optionsToHideCount > 1) {
                    // Randomly pick half of the incorrect options to hide
                    val optionsToHide =
                        incorrectOptions
                            .shuffled()
                            .take(optionsToHideCount)

                    // Hide the selected options
                    optionsToHide.forEach { it.visibility = View.INVISIBLE }

                    // Deduct 5 coins
                    deductCoins(5)
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
            handleLevelCompletion()
        } else {
            displayCurrentQuestion()
        }
    }

    private fun handleLevelCompletion() {
        currentQuestionIndex = 0
        _questionProgress.value = "0 / ${questions.size}"
        val nextLevel = (currentLevel.value ?: 0) + 1

        preferenceManager.saveCurrentLevel(nextLevel)

        viewModelScope.launch {
            questions.forEach { question ->
                questionRepository.markQuestionAsSolved(question.id)
            }
        }

        // Mark the current day as completed
        streakTrackerView.markDayAsCompleted()
        val isStreakCompleted = streakTrackerView.isStreakCompleted()

        _currentLevel.value = nextLevel

        if (questions.isEmpty()) {
            _navigationEvent.value = NavigationEvent.NavigateToResult
        } else {
            _navigationEvent.value =
                NavigationEvent.ShowLevelComplete(
                    level = nextLevel,
                    isStreakCompleted = isStreakCompleted,
                )
        }
    }

    private fun addTime(extraTimeInMillis: Long) {
        timerManager.addTimeDuration(extraTimeInMillis)
    }

    private fun startQuestionTimer() {
        timerManager.startTimer(
            onTick = { seconds ->
                _timeLeft.value = seconds
            },
            onFinish = {
                _showTimeUpDialog.value = true
                disableAnswerButtons()
            },
        )
    }

    private fun stopTimer() {
        timerManager.stopTimer()
    }

    fun buyMoreTime() {
        val extraTime = 10000L
        val timeCost = 5

        _coins.value?.let { currentCoins ->
            if (currentCoins >= timeCost) {
                deductCoins(timeCost)
                _showTimeUpDialog.value = false
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
        _showTimeUpDialog.value = false
        generateLevel()
        startQuestionTimer()
        enableAnswerButtons()
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

    fun hasEnoughCoins(coinAmount: Int): Boolean = coinAmount <= coins.value!!

    fun deductCoins(amount: Int = 5) {
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
            onTick = { seconds ->
                _timeLeft.value = seconds
            },
            onFinish = {
                _showTimeUpDialog.value = true
                disableAnswerButtons()
            },
        )
        _areAnswerButtonsEnabled.value = true // Enable answer buttons when resuming
    }

    sealed class NavigationEvent {
        data class ShowToast(
            val message: String,
        ) : NavigationEvent()

        data class ShowLevelComplete(
            val level: Int,
            val isStreakCompleted: Boolean,
        ) : NavigationEvent()

        data object NavigateToResult : NavigationEvent()
    }

    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val preferenceManager = (this[APPLICATION_KEY] as QuizApplication).preferenceManager
                    val questionRepository = (this[APPLICATION_KEY] as QuizApplication).questionRepository
                    val streakTrackerView = (this[APPLICATION_KEY] as QuizApplication).streakTrackerView
                    val timerManager = TimerManager()

                    QuizViewModel(
                        preferenceManager = preferenceManager,
                        questionRepository = questionRepository,
                        streakTrackerView = streakTrackerView,
                        timerManager = timerManager,
                    )
                }
            }
    }
}
