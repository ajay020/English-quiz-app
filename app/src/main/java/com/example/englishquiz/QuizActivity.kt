package com.example.englishquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.englishquiz.data.Question
import com.example.englishquiz.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var optionButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.btnHint.setOnClickListener {
            viewModel.useHint()
        }

        binding.btnTimerBooster.setOnClickListener {
            viewModel.buyMoreTime()
        }

        binding.btnNext.setOnClickListener {
            resetOptions()
            viewModel.onNextQuestion()
        }

        binding.btnQuestionNum.setOnClickListener {
            showQuestionCountDialog()
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

        viewModel.isNextButtonVisible.observe(this) { isVisible ->
            binding.btnNext.visibility = if (isVisible) View.VISIBLE else View.GONE
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
        if (currentQuestion?.correctAnswer == selectedButton.text) {
            selectedButton.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
            )
        } else {
            selectedButton.setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_red_light),
            )
        }
        binding.btnHint.isEnabled = false
    }

    private fun resetOptions() {
        optionButtons.forEach { button ->
            button.isEnabled = true
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }
    }

    private fun handleNavigationEvent(event: QuizViewModel.NavigationEvent) {
        when (event) {
            is QuizViewModel.NavigationEvent.ShowToast -> {
                Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
            }
            is QuizViewModel.NavigationEvent.ShowLevelComplete -> {
                AlertDialog
                    .Builder(this)
                    .setTitle("Level Complete!")
                    .setMessage("You have completed Level ${event.level}!")
                    .setPositiveButton("Start Level ${event.level + 1}") { _, _ ->
                        Log.d("quizActivity", "start next level")
                        viewModel.generateLevel()
                    }.setCancelable(false)
                    .show()
            }
            is QuizViewModel.NavigationEvent.NavigateToResult -> {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("score", event.score)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showQuestionCountDialog() {
        val options = arrayOf("2", "3", "4")
        AlertDialog
            .Builder(this)
            .setTitle("Choose Number of Questions")
            .setItems(options) { _, which ->
                val count = options[which].toInt()
                viewModel.setUpQuestionCount(count)
            }.show()
    }

    private fun showTimeUpDialog() {
        AlertDialog
            .Builder(this)
            .setTitle("Time's Up!")
            .setMessage("Would you like to restart the level or buy more time for 10 coins?")
            .setPositiveButton("Buy More Time") { _, _ ->
                viewModel.buyMoreTime()
            }.setNegativeButton("Restart Level") { _, _ ->
                viewModel.restartLevel()
            }.setCancelable(false)
            .show()
    }
}
