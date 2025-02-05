package com.example.quizzybee.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.quizzybee.R
import com.example.quizzybee.data.Question
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.databinding.ActivityQuizBinding
import com.example.quizzybee.utils.constants.AppConstants.HINT_COST
import com.example.quizzybee.utils.constants.AppConstants.TIMER_BOOST_COST
import com.example.quizzybee.utils.managers.DialogManager
import com.example.quizzybee.utils.managers.SoundManager
import com.example.quizzybee.viewmodel.QuizViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizActivity : BaseActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var optionButtons: List<MaterialButton>

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)
        soundManager = SoundManager(preferenceManager, this)
        dialogManager = DialogManager(this, soundManager, preferenceManager)

        // Initialize the progress bar
        binding.progressBar.max = 100

        if (preferenceManager.isMusicEnabled())
            {
                soundManager.startMusic()
            }

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        viewModel.questionProgress.observe(this) { questionProgress ->
            binding.tvQuestionProgress.text = questionProgress
        }

        optionButtons =
            listOf(
                binding.btnOption1,
                binding.btnOption2,
                binding.btnOption3,
                binding.btnOption4,
            ) as List<MaterialButton>

        optionButtons.forEach { button ->
            button.setOnClickListener {
                handleOptionClick(button)
            }
            button.setTextAppearance(R.style.CustomOutlinedButton)
        }

        binding.btnPause.setOnClickListener {
            soundManager.playButtonClickSound()
            viewModel.pauseGame()
            dialogManager.showPauseDialog(
                onResume = { viewModel.resumeGame() },
                onQuit = { finish() },
            )
        }

        binding.btnHint.setOnClickListener {
            soundManager.playHintSound()
            viewModel.useHint(optionButtons)
        }

        binding.btnTimerBooster.setOnClickListener {
            soundManager.playHintSound()
            viewModel.buyMoreTime()
        }
    }

    private fun updateBoostAndHintAvailability(totalCoins: Int) {
        val hintCount = totalCoins / HINT_COST
        val timerBoostCount = totalCoins / TIMER_BOOST_COST

        // Update badge visibility and count
        binding.badgeHint.text = hintCount.toString()
        binding.badgeHint.visibility = if (hintCount > 0) View.VISIBLE else View.GONE

        binding.badgeTimerBooster.text = timerBoostCount.toString()
        binding.badgeTimerBooster.visibility = if (timerBoostCount > 0) View.VISIBLE else View.GONE

        // Enable/disable buttons based on availability
        binding.btnHint.isEnabled = hintCount > 0
        binding.btnTimerBooster.isEnabled = timerBoostCount > 0
    }

    private fun setupObservers() {
        viewModel.timeLeft.observe(this) { seconds ->
            binding.tvTimer.text = getString(R.string.time_format, seconds)

            // Change color to red when time is running out
            val warningColor = resolveColorAttribute(R.attr.timerColorWarning)
            val defaultColor = resolveColorAttribute(R.attr.timerColorDefault)
            binding.tvTimer.setTextColor(if (seconds <= 5) warningColor else defaultColor)
        }

        viewModel.smoothProgress.observe(this) { progress ->
            binding.progressBar.progress = (progress * 100).toInt()
        }

        viewModel.areAnswerButtonsEnabled.observe(this) { enabled ->
            optionButtons.forEach { it.isEnabled = enabled }
        }

        viewModel.currentQuestion.observe(this) { question ->
            question?.let { displayQuestion(it) }
        }

        viewModel.currentLevel.observe(this) { level ->
            binding.tvLevel.text = getString(R.string.level_text, level)
        }

        viewModel.coins.observe(this) { coins ->
            val startValue = viewModel.getLastCoinValue()

            binding.coinDisplay.setAnimatedCoinCount(
                startValue = startValue,
                endValue = coins,
                duration = 200L,
            )

            updateBoostAndHintAvailability(coins)
        }

        viewModel.navigationEvent.observe(this) { event ->
            handleNavigationEvent(event)
        }
    }

    private fun displayQuestion(question: Question) {
        binding.tvQuestion.text = question.questionText
        optionButtons.forEachIndexed { index, button ->
            button.text = "${question.options[index]}"
        }
        resetOptions()
    }

    private fun handleOptionClick(selectedButton: MaterialButton) {
        optionButtons.forEach { it.isEnabled = false }
        viewModel.checkAnswer(selectedButton.text.toString())

        // Update UI to show correct/incorrect answer
        val currentQuestion = viewModel.currentQuestion.value

        lifecycleScope.launch {
            if (currentQuestion?.correctAnswer == selectedButton.text) {
                // Play sound for correct option
                soundManager.playCorrectAnswerSound()
                // Update button for correct answer
                selectedButton.setTextColor(getColor(R.color.green))
                selectedButton.strokeColor = ColorStateList.valueOf(getColor(R.color.green))
                selectedButton.strokeWidth = 3

                delay(700)
                viewModel.onNextQuestion()
            } else {
                // Play sound for incorrect answer
                soundManager.playIncorrectAnswerSound()

                // Update button for incorrect answer
                selectedButton.setTextColor(getColor(R.color.red))
                selectedButton.strokeColor = ColorStateList.valueOf(getColor(R.color.red))
                selectedButton.strokeWidth = 3

                delay(700)
                viewModel.onNextQuestion()
            }
        }
    }

    private fun resetOptions() {
        optionButtons.forEach { button ->
            button.isEnabled = true
            button.visibility = View.VISIBLE

            // Reset colors
            val strokeColor =
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorOnSurface,
                    Color.BLACK,
                )
            button.setTextColor(ColorStateList.valueOf(strokeColor))
            button.strokeColor = ColorStateList.valueOf(strokeColor)
            button.strokeWidth = 1
        }
    }

    private fun handleNavigationEvent(event: QuizViewModel.NavigationEvent) {
        when (event) {
            is QuizViewModel.NavigationEvent.ShowToast -> {
                Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
            }

            is QuizViewModel.NavigationEvent.ShowQuizComplete -> {
                soundManager.playLevelCompleteSound()

                dialogManager.showQuizCompleteDialog(
                    event.level,
                    event.correctQuestions,
                    event.totalQuestions,
                    event.isStreakCompleted,
                    onCoinsUpdated = { amount ->
                        viewModel.addCoins(amount)
                    },
                    onStartNextLevel = { viewModel.generateLevel() },
                    onRetry = { viewModel.restartLevel() },
                    saveSolvedQuestions = { viewModel.saveSolvedQuestions() },
                )
            }

            is QuizViewModel.NavigationEvent.NavigateToResult -> {
                preferenceManager.saveQuizCompletionDate()
                val intent = Intent(this, ResultActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun resolveColorAttribute(attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    override fun onPause() {
        super.onPause()
        soundManager.stopMusic()
    }

    override fun onResume() {
        super.onResume()
        soundManager.startMusic()
    }
}
