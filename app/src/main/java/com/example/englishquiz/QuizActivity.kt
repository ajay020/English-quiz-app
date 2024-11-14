package com.example.englishquiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.englishquiz.data.Level
import com.example.englishquiz.databinding.ActivityQuizBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var levels: List<Level>
    private var currentLevelIndex = 0
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        levels = loadLevels()
        loadProgress()
        displayQuestion()

        if (currentLevelIndex < levels.size) {
            binding.tvLevel.text = "Level ${levels[currentLevelIndex].level}"
        }
        binding.tvCoins.text = "Coins: ${getCoins()}"

        binding.btnHint.setOnClickListener {
            useHint()
        }

        val optionButtons =
            listOf(
                binding.btnOption1,
                binding.btnOption2,
                binding.btnOption3,
                binding.btnOption4,
            )

        optionButtons.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button, optionButtons)
            }
        }

        binding.btnNext.setOnClickListener {
            currentQuestionIndex++
            resetOptions(optionButtons)
            if (currentQuestionIndex >= levels[currentLevelIndex].questions.size) {
                currentQuestionIndex = 0
                currentLevelIndex++
                binding.tvLevel.text = "Level ${ currentLevelIndex + 1 }"
                saveProgress()

                if (currentLevelIndex >= levels.size) {
                    showCompletionScreen()
                } else {
                    showNextLevelScreen()
                }
            } else {
                resetOptions(optionButtons)
                displayQuestion()
            }
        }
    }

    private fun displayQuestion() {
        if (currentLevelIndex < levels.size) {
            val currentLevel = levels[currentLevelIndex]
            val question = currentLevel.questions[currentQuestionIndex]
            binding.tvQuestion.text = question.question
            binding.btnOption1.text = question.options[0]
            binding.btnOption2.text = question.options[1]
            binding.btnOption3.text = question.options[2]
            binding.btnOption4.text = question.options[3]
        }

        // Reset hint button
        binding.btnHint.isEnabled = true
        binding.btnHint.setBackgroundColor(ContextCompat.getColor(this, R.color.default_button))
    }

    private fun showNextLevelScreen() {
        val coins = getCoins() + 5
        saveCoins(coins)
        binding.tvCoins.text = "Coins: $coins"

        // Display a congratulatory message and the option to proceed to the next level.
        AlertDialog
            .Builder(this)
            .setTitle("Level Complete!")
            .setMessage(
                "You have completed Level ${levels[currentLevelIndex - 1].level}!",
            ).setPositiveButton("Next Level") { _, _ ->
                displayQuestion()
            }.setCancelable(false)
            .show()
    }

    private fun showCompletionScreen() {
        // Display a final completion message and score.
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }

    private fun checkAnswer(
        button: Button,
        optionButtons: List<Button>,
    ) {
        val currentLevel = levels[currentLevelIndex]
        val question = currentLevel.questions[currentQuestionIndex]
        val correctAnswer = question.correctAnswer

        // Disable all buttons after selection
        optionButtons.forEach { it.isEnabled = false }

        // Check if the selected answer is correct
        if (button.text == correctAnswer) {
            button.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light,
                ),
            )
            score++
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            // Highlight the correct answer
            optionButtons
                .find { it.text == correctAnswer }
                ?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        }

        // Show the "Next" button
        binding.btnNext.visibility = View.VISIBLE
        binding.btnHint.isEnabled = false // Disable hint after answering
    }

    private fun resetOptions(optionButtons: List<Button>) {
        optionButtons.forEach {
            it.isEnabled = true
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }
        binding.btnNext.visibility = View.GONE
    }

    private fun useHint() {
        val cost = 5
        val currentCoins = getCoins()

        if (currentCoins >= cost) {
            saveCoins(currentCoins - cost)
            updateCoinDisplay()

            // Highlight the correct option
            val currentQuestion = levels[currentLevelIndex].questions[currentQuestionIndex]
            val correctAnswer = currentQuestion.correctAnswer

            val optionButtons =
                listOf(
                    binding.btnOption1,
                    binding.btnOption2,
                    binding.btnOption3,
                    binding.btnOption4,
                )

            optionButtons.forEach { button ->
                if (button.text == correctAnswer) {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                }
            }

            // Disable the hint button to prevent reuse for this question
            binding.btnHint.isEnabled = false
        } else {
            // Show a message if the user doesn't have enough coins
            Toast.makeText(this, "Not enough coins for a hint!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadLevels(): List<Level> {
        val jsonString = assets.open("quizzes.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, object : TypeToken<List<Level>>() {}.type)
    }

    private fun saveProgress() {
        val prefs = getSharedPreferences("QuizApp", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt("currentLevelIndex", currentLevelIndex)
            apply()
        }
    }

    private fun loadProgress() {
        val prefs = getSharedPreferences("QuizApp", Context.MODE_PRIVATE)
        currentLevelIndex = prefs.getInt("currentLevelIndex", 0)
    }

    private fun getCoins(): Int {
        val prefs = getSharedPreferences("QuizApp", Context.MODE_PRIVATE)
        return prefs.getInt("coins", 0)
    }

    private fun updateCoinDisplay() {
        val coins = getCoins()
        binding.tvCoins.text = "Coins: $coins"
    }

    private fun saveCoins(coins: Int) {
        val prefs = getSharedPreferences("QuizApp", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt("coins", coins)
            apply()
        }
    }
}
