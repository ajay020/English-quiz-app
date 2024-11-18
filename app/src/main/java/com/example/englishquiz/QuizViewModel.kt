package com.example.englishquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.englishquiz.data.Level
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.QuizRepository
import com.example.englishquiz.data.SharedPreferenceStorage

class QuizViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val preferenceStorage = SharedPreferenceStorage(application)
    private val repository = QuizRepository(application, preferenceStorage)
    private val timerManager = TimerManager()

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _coins = MutableLiveData(0)
    val coins: LiveData<Int> = _coins

    private val _currentLevel = MutableLiveData<Level?>(null)
    val currentLevel: LiveData<Level?> = _currentLevel

    private val _isNextButtonVisible = MutableLiveData(false)
    val isNextButtonVisible: LiveData<Boolean> = _isNextButtonVisible

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _showTimeUpDialog = MutableLiveData<Boolean>()
    val showTimeUpDialog: LiveData<Boolean> = _showTimeUpDialog

    private val _areAnswerButtonsEnabled = MutableLiveData(true)
    val areAnswerButtonsEnabled: LiveData<Boolean> = _areAnswerButtonsEnabled

    private var questions: List<Question> = emptyList()
    private val solvedQuestions = mutableSetOf<Int>()
    private var currentLevelNumber = 1
    private var currentQuestionIndex = 0
    private var questionCountPerLevel = 3

    init {
        initializeQuizState()
    }

    private fun initializeQuizState() {
        questions = repository.loadQuestionsFromJson()
        solvedQuestions.addAll(repository.getSolvedQuestions())
        currentLevelNumber = repository.getCurrentLevel()
        questionCountPerLevel = repository.getQuestionCount()
        _coins.value = repository.getCoins()
        generateLevel()
    }

    fun setUpQuestionCount(questionCount: Int) {
        questionCountPerLevel = questionCount
        repository.saveQuestionCount(questionCount)
    }

    fun generateLevel() {
        solvedQuestions.clear()
        if (questionCountPerLevel > questions.size) {
            _navigationEvent.value = NavigationEvent.ShowToast("Not enough questions available!")
            return
        }

        val unsolvedQuestions = questions.filter { it.id !in solvedQuestions }
        val currLevelQuestions = unsolvedQuestions.take(questionCountPerLevel)

        currentLevelNumber = repository.getCurrentLevel()
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
            }
            _isNextButtonVisible.value = true
        }
    }

    fun useHint() {
        val hintCost = 5
        _coins.value?.let { currentCoins ->
            if (currentCoins >= hintCost) {
                val newCoins = currentCoins - hintCost
                repository.saveCoins(newCoins)
                _coins.value = newCoins
            } else {
                _navigationEvent.value = NavigationEvent.ShowToast("Not enough coins for a hint!")
            }
        }
    }

    fun onNextQuestion() {
        stopTimer() // Stop timer before moving to next question
        currentQuestionIndex++
        _isNextButtonVisible.value = false

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
        val nextLevel = (currentLevel.value?.level ?: 0) + 1

        repository.saveCurrentLevel(nextLevel)
        repository.saveSolvedQuestions(solvedQuestions)
        _currentLevel.value?.let {
            it.level = nextLevel
        }

        if (nextLevel >= questions.size) {
            _navigationEvent.value = NavigationEvent.NavigateToResult(_score.value ?: 0)
        } else {
            val newCoins = (_coins.value ?: 0) + 5
            repository.saveCoins(newCoins)
            _coins.value = newCoins
            _navigationEvent.value = NavigationEvent.ShowLevelComplete(nextLevel)
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
        val timeCost = 0

        _coins.value?.let { currentCoins ->
            if (currentCoins >= timeCost) {
                val newCoins = currentCoins - timeCost
                repository.saveCoins(newCoins)
                _coins.value = newCoins
                _showTimeUpDialog.value = false
//                startQuestionTimer()
                addTime(extraTime)
                enableAnswerButtons()
            } else {
                _navigationEvent.value = NavigationEvent.ShowToast("Not enough coins!")
            }
        }
    }

    fun restartLevel() {
        currentQuestionIndex = 0
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

    sealed class NavigationEvent {
        data class ShowToast(
            val message: String,
        ) : NavigationEvent()

        data class ShowLevelComplete(
            val level: Int,
        ) : NavigationEvent()

        data class NavigateToResult(
            val score: Int,
        ) : NavigationEvent()

//        object ShowTimeUpDialog : NavigationEvent()
//
//        object RestartLevel : NavigationEvent()
    }
}
