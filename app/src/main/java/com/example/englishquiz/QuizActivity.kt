package com.example.englishquiz

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.englishquiz.data.Question
import com.example.englishquiz.databinding.ActivityQuizBinding
import com.example.englishquiz.databinding.DialogRecoveryBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var optionButtons: List<Button>
    private lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogManager = DialogManager(this)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.tvTimer.apply {
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        optionButtons =
            listOf(
                binding.btnOption1,
                binding.btnOption2,
                binding.btnOption3,
                binding.btnOption4,
            )

        optionButtons.forEach { button ->
            button.setOnClickListener {
                handleOptionClick(button)
            }
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
            if (seconds <= 5) {
                binding.tvTimer.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            } else {
                binding.tvTimer.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }
        }

        viewModel.showTimeUpDialog.observe(this) { show ->
            if (show) {
                showTimeUpDialog()
            }
        }

        viewModel.areAnswerButtonsEnabled.observe(this) { enabled ->
            optionButtons.forEach { it.isEnabled = enabled }
        }

        viewModel.currentQuestion.observe(this) { question ->
            question?.let { displayQuestion(it) }
        }

        viewModel.currentLevel.observe(this) { level ->
            binding.tvLevel.text = "Level ${level?.level}"
        }

        viewModel.coins.observe(this) { coins ->
            binding.tvCoins.text = "Coins: $coins"
            binding.btnHint.isEnabled = coins >= 5
        }

        viewModel.navigationEvent.observe(this) { event ->
            handleNavigationEvent(event)
        }
    }

    private fun displayQuestion(question: Question) {
        binding.tvQuestion.text = question.question
        optionButtons.forEachIndexed { index, button ->
            button.text = question.options[index]
        }
        binding.btnHint.isEnabled = true
        resetOptions()
    }

    private fun handleOptionClick(selectedButton: Button) {
        optionButtons.forEach { it.isEnabled = false }
        viewModel.checkAnswer(selectedButton.text.toString())

        // Update UI to show correct/incorrect answer
        val currentQuestion = viewModel.currentQuestion.value

        lifecycleScope.launch {
            if (currentQuestion?.correctAnswer == selectedButton.text) {
                viewModel.animateButtonColor(
                    this@QuizActivity,
                    selectedButton,
                    android.R.color.transparent,
                    android.R.color.holo_green_light,
                )
                delay(800) // Wait for 1 second
                viewModel.onNextQuestion()
            } else {
                viewModel.animateButtonColor(
                    this@QuizActivity,
                    selectedButton,
                    android.R.color.transparent,
                    android.R.color.holo_red_light,
                )
                delay(800) // Wait for 1 second
                showRecoveryDialog()
            }
        }
    }

    private fun resetOptions() {
        optionButtons.forEach { button ->
            button.isEnabled = true
            button.visibility = View.VISIBLE
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }
    }

    private fun handleNavigationEvent(event: QuizViewModel.NavigationEvent) {
        when (event) {
            is QuizViewModel.NavigationEvent.ShowToast -> {
                Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
            }
            is QuizViewModel.NavigationEvent.ShowLevelComplete -> {
                dialogManager.showLevelCompleteDialog(event.level) {
                    viewModel.generateLevel()
                }
            }
            is QuizViewModel.NavigationEvent.NavigateToResult -> {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("score", event.score)
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
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle restart button
        binding.btnRestart.setOnClickListener {
            viewModel.restartLevel()
            dialog.dismiss()
        }

        dialog.show()
    }
}
