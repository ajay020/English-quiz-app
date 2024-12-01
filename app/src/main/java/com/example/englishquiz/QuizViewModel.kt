package com.example.englishquiz

import android.app.Application
import android.view.View
import android.widget.Button
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.englishquiz.data.Level
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.QuizRepository
import com.example.englishquiz.data.SharedPreferenceStorage
import kotlin.math.ceil

class QuizViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val preferenceStorage = SharedPreferenceStorage(application)
    private val repository = QuizRepository(application, preferenceStorage)

    private val preferenceManager = PreferenceManager(application.applicationContext)
    private val streakTrackerView = StreakTrackerView(application.applicationContext)

    private val timerManager = TimerManager()

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _coins = MutableLiveData(200)
    val coins: LiveData<Int> = _coins

    private val _currentLevel = MutableLiveData<Level?>(null)
    val currentLevel: LiveData<Level?> = _currentLevel

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _questionNumber = MutableLiveData<Int>(0)
    val questionNumber: LiveData<Int> = _questionNumber

    private val _showTimeUpDialog = MutableLiveData<Boolean>()
    val showTimeUpDialog: LiveData<Boolean> = _showTimeUpDialog

    private val _areAnswerButtonsEnabled = MutableLiveData(true)
    val areAnswerButtonsEnabled: LiveData<Boolean> = _areAnswerButtonsEnabled

    private var questions: List<Question> = emptyList()
    private val solvedQuestions = mutableSetOf<Int>()
    private var currentLevelNumber = 1
    private var currentQuestionIndex = 0
    private var questionCountPerLevel = 3
    private var lastCoinValue = 0

    init {
        initializeQuizState()
    }

    private fun initializeQuizState() {
        questions = repository.loadQuestionsFromJson()
        solvedQuestions.addAll(repository.getSolvedQuestions())
        currentLevelNumber = preferenceManager.getCurrentLevel()
        questionCountPerLevel = repository.getQuestionCount()
        _coins.value = preferenceManager.getCoins()
        generateLevel()
    }

    fun setUpQuestionCount(questionCount: Int) {
        questionCountPerLevel = questionCount
        repository.saveQuestionCount(questionCount)
    }

    fun generateLevel() {
        solvedQuestions.clear()
        solvedQuestions.addAll(repository.getSolvedQuestions())

        val unsolvedQuestions = questions.filter { it.id !in solvedQuestions }
        val currLevelQuestions = unsolvedQuestions.take(questionCountPerLevel)

        currentLevelNumber = preferenceManager.getCurrentLevel()
        _currentLevel.value = Level(currentLevelNumber, currLevelQuestions)
        solvedQuestions.addAll(currLevelQuestions.map { it.id })
        displayCurrentQuestion()
    }

    private fun displayCurrentQuestion() {
        currentLevel.value?.let { level ->
            if (level.questions.isNotEmpty()) {
                _currentQuestion.value = level.questions[currentQuestionIndex]
                startQuestionTimer() // Start timer for new question
            } else {
                _navigationEvent.value = NavigationEvent.NavigateToResult(_score.value ?: 0)
            }
        }
    }

    fun checkAnswer(selectedAnswer: String) {
        stopTimer() // Stop timer when answer is selected
        _currentQuestion.value?.let { question ->
            if (selectedAnswer == question.correctAnswer) {
                _score.value = (_score.value ?: 0) + 1
                addCoins(2)
            }
        }
    }

    fun useHint(optionButtons: List<Button>) {
        val hintCost = 5

        _coins.value?.let { currentCoins ->
            if (currentCoins >= hintCost) {
                val currentQuestion = currentQuestion.value ?: return
                val incorrectOptions = optionButtons.filter { it.text != currentQuestion.correctAnswer }

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
        _questionNumber.value = currentQuestionIndex

        currentLevel.value?.let { level ->
            if (currentQuestionIndex >= level.questions.size) {
                handleLevelCompletion()
            } else {
                displayCurrentQuestion()
            }
        }
    }

    private fun handleLevelCompletion() {
        currentQuestionIndex = 0
        _questionNumber.value = 0
        val nextLevel = (currentLevel.value?.level ?: 0) + 1

        preferenceManager.saveCurrentLevel(nextLevel)
        repository.saveSolvedQuestions(solvedQuestions)

        // Mark the current day as completed
        streakTrackerView.markDayAsCompleted()
        val isStreakCompleted = streakTrackerView.isStreakCompleted()

        _currentLevel.value?.let {
            it.level = nextLevel
        }

        if (nextLevel >= questions.size) {
            _navigationEvent.value = NavigationEvent.NavigateToResult(_score.value ?: 0)
        } else {
            _navigationEvent.value =
                NavigationEvent
                    .ShowLevelComplete(
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
        _questionNumber.value = 0
        _score.value = 0
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

        data class NavigateToResult(
            val score: Int,
        ) : NavigationEvent()

//        object ShowTimeUpDialog : NavigationEvent()
//
//        object RestartLevel : NavigationEvent()
    }
}
