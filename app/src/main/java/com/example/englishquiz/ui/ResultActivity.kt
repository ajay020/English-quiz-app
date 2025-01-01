package com.example.englishquiz.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.englishquiz.R
import com.example.englishquiz.databinding.ActivityResultBinding
import com.example.englishquiz.viewmodel.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ResultActivity : BaseActivity() {
    lateinit var binding: ActivityResultBinding

    val viewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupListeners()
        displayTotalTime(preferenceManager.getTotalTimeSpent())
    }

    private fun setupViews() {
        // Observe question count from ViewModel and set it to the UI
        displayQuestionCount()

        // Observe days between launch and completion from ViewModel and set it to the UI
        displayDaysBetweenLaunchAndCompletion()
    }

    private fun displayQuestionCount() {
        lifecycleScope.launch {
            viewModel.questionCount.collectLatest { count ->
                binding.tvQuestionSolved.text = getString(R.string.total_questions_solved, count)
            }
        }
    }

    private fun displayDaysBetweenLaunchAndCompletion() {
        lifecycleScope.launch {
            viewModel.daysBetweenLaunchAndCompletion.collect { days ->
                binding.tvDaysSpent.text = getString(R.string.days_spent_message, days)
            }
        }
    }

    private fun setupListeners() {
        binding.btnHome.setOnClickListener {
            finish()
        }

        binding.btnPlayAgain.setOnClickListener {
            viewModel.resetQuestions()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnShareAchievement.setOnClickListener {
            shareStats()
        }
    }

    private fun displayTotalTime(totalTime: Long) {
        val minutes = (totalTime / (1000 * 60)) % 60
        val hours = (totalTime / (1000 * 60 * 60))

        binding.tvTotalTime.text = getString(R.string.total_time_spent, hours, minutes)
    }

    private fun shareStats() {
        // Text content for sharing
        val statsText = "🎉 I completed all quizzes on QuizzyBee!\nTotal Time: 5 hours\nDays Spent: 3 days"

        // Create a file in the internal storage
        val imageFile = File(filesDir, "images/share_image.png")
        if (!imageFile.parentFile.exists()) {
            imageFile.parentFile.mkdirs()
        }

        // Copy image from drawable to internal storage
        try {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.share_image)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            Log.e("Share", "Error copying image to internal storage", e)
            return
        }

        // Get URI using FileProvider
        val imageUri =
            FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                imageFile,
            )

        // Grant temporary URI permission explicitly
        grantUriPermission(
            packageManager
                .resolveActivity(
                    Intent(Intent.ACTION_SEND).apply { type = "image/png" },
                    PackageManager.MATCH_DEFAULT_ONLY,
                )?.activityInfo
                ?.packageName ?: "",
            imageUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )

        // Create share intent
        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, statsText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        // Start the share intent chooser
        startActivity(Intent.createChooser(shareIntent, "Share your QuizzyBee stats"))
    }
}
