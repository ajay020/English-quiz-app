package com.example.englishquiz.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.englishquiz.R
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.databinding.ActivityQuizBinding
import com.example.englishquiz.databinding.DialogRecoveryBinding
import com.example.englishquiz.utils.managers.DialogManager
import com.example.englishquiz.utils.managers.SoundManager
import com.example.englishquiz.viewmodel.QuizViewModel
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
            viewModel.pauseGame()
            dialogManager.showPauseDialog(
                onResume = { viewModel.resumeGame() },
                onQuit = { finish() },
            )
        }

        binding.btnHint.setOnClickListener {
            viewModel.useHint(optionButtons)
        }

        binding.btnTimerBooster.setOnClickListener {
            viewModel.buyMoreTime()
        }
    }

    private fun setupObservers() {
        viewModel.timeLeft.observe(this) { seconds ->
            binding.tvTimer.text = "Time: $seconds s"

            // Change color to red when time is running out
            val warningColor = resolveColorAttribute(R.attr.timerColorWarning)
            val defaultColor = resolveColorAttribute(R.attr.timerColorDefault)
            binding.tvTimer.setTextColor(if (seconds <= 5) warningColor else defaultColor)

            binding.progressBar.progress = seconds.toInt()
            binding.timerText.text = "$seconds"
        }

        viewModel.showTimeUpDialog.observe(this) { show ->
            if (show) {
//                showTimeUpDialog()
            }
        }

        viewModel.areAnswerButtonsEnabled.observe(this) { enabled ->
            optionButtons.forEach { it.isEnabled = enabled }
        }

        viewModel.currentQuestion.observe(this) { question ->
            question?.let { displayQuestion(it) }
        }

        viewModel.currentLevel.observe(this) { level ->
            binding.tvLevel.text = "Level $level"
        }

        viewModel.coins.observe(this) { coins ->
            val startValue = viewModel.getLastCoinValue()

            binding.coinDisplay.setAnimatedCoinCount(
                startValue = startValue,
                endValue = coins,
                duration = 200L,
            )

            // Enable/disable hint button
            binding.btnHint.isEnabled = coins >= 5
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
        binding.btnHint.isEnabled = true
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

    private fun showTimeUpDialog() {
        dialogManager.showTimeUpDialog(
            onBuyMoreTime = { viewModel.buyMoreTime() },
            onRestartLevel = { viewModel.restartLevel() },
        )
    }

    private fun showRecoveryDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val binding = DialogRecoveryBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        // Handle recovery button
        binding.btnRecover.setOnClickListener {
            if (viewModel.hasEnoughCoins(10)) {
                viewModel.deductCoins(10)
                resetOptions()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle restart button
        binding.btnRestart.setOnClickListener {
            viewModel.restartLevel()
            resetOptions()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun resolveColorAttribute(attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}
