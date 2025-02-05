package com.example.quizzybee.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.quizzybee.R
import com.example.quizzybee.databinding.ActivityResultBinding
import com.example.quizzybee.viewmodel.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

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
            val totalTime = preferenceManager.getTotalTimeSpent()
            val daysSpent = preferenceManager.getDaysBetweenLaunchAndCompletion()

            shareCombinedImage(totalTime, daysSpent)
        }
    }

    private fun displayTotalTime(totalTime: Long) {
        val minutes = (totalTime / (1000 * 60)) % 60
        val hours = (totalTime / (1000 * 60 * 60))

        binding.tvTotalTime.text = getString(R.string.total_time_spent, hours, minutes)
    }

    private fun shareCombinedImage(
        totalTime: Long,
        daysSpent: Int,
    ) {
        val minutes = ((totalTime / (1000 * 60)) % 60).toInt()
        val hours = (totalTime / (1000 * 60 * 60)).toInt()

        val bitmap = createImageWithText(daysSpent, hours, minutes)
        val imageFile = saveBitmapToInternalStorage(bitmap)

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

        // Share Intent
        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        startActivity(Intent.createChooser(shareIntent, "Share your QuizzyBee stats"))
    }

    private fun createImageWithText(
        daysSpent: Int,
        hours: Int,
        minutes: Int,
    ): Bitmap {
        val baseBitmap = BitmapFactory.decodeResource(resources, R.drawable.share_image)

        // Define dimensions for combined bitmap
        val width = baseBitmap.width
        val textHeight = 200 // Space for text below the image
        val combinedHeight = baseBitmap.height + textHeight

        // Create a new bitmap with the combined dimensions
        val combinedBitmap = Bitmap.createBitmap(width, combinedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        canvas.drawColor(Color.WHITE) // Set background color

        // Draw the base image
        canvas.drawBitmap(baseBitmap, 0f, 0f, null)

        // Draw the text below the image
        val paint =
            Paint().apply {
                color = Color.BLACK
                textSize = 32f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

        val text = getString(R.string.accomplishment_message, hours, minutes, daysSpent)
        val x = (width / 2).toFloat()
        val y = (baseBitmap.height + 20).toFloat()
        val textLines = text.split("\n")

        textLines.forEachIndexed { index, line ->
            canvas.drawText(line, x, y + (index * 42), paint)
        }

        return combinedBitmap
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): File {
        val imageFile = File(filesDir, "images/progress_share_image.png")
        if (imageFile.parentFile?.exists() == false) {
            imageFile.parentFile?.mkdirs()
        }

        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return imageFile
    }
}
