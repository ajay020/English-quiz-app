package com.example.englishquiz

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.englishquiz.databinding.DialogLevelCompleteBinding
import com.example.englishquiz.databinding.DialogTimeUpBinding

class DialogManager(
    private val context: Context,
) {
    fun showSettingsDialog(
        onAudioChanged: (Boolean) -> Unit,
        onMusicChanged: (Boolean) -> Unit,
        onThemeChanged: (String) -> Unit,
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_settings)
        dialog.setCancelable(false)

        val soundButton = dialog.findViewById<ImageButton>(R.id.button_sound)
        val musicButton = dialog.findViewById<ImageButton>(R.id.button_music)

        // Initially, assume both sound and music are enabled
        var isSoundEnabled = true
        var isMusicEnabled = true

        soundButton.setOnClickListener {
            isSoundEnabled = !isSoundEnabled
            soundButton.setImageResource(if (isSoundEnabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
            // Start or stop sound logic here
            onAudioChanged(isSoundEnabled)
        }

        musicButton.setOnClickListener {
            isMusicEnabled = !isMusicEnabled
            musicButton.setImageResource(if (isMusicEnabled) R.drawable.ic_music_on else R.drawable.ic_music_off)
            onMusicChanged(isMusicEnabled)
        }

        val closeButton = dialog.findViewById<Button>(R.id.button_close_settings)

        closeButton.setOnClickListener {
            dialog.dismiss()
//            onThemeChanged("Light")
            // Handle theme change logic here
        }
        dialog.show()
    }

    fun showPauseDialog(
        onResume: () -> Unit,
        onQuit: () -> Unit,
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_pause)
        dialog.setCancelable(false) // Disable dismissing by tapping outside

        // Find views in the custom dialog layout
        val btnResume = dialog.findViewById<Button>(R.id.btnResume)
        val btnQuit = dialog.findViewById<Button>(R.id.btnQuit)

        btnResume.setOnClickListener {
            dialog.dismiss() // Close the dialog and resume the game
            onResume() // Call method to resume the game
        }

        btnQuit.setOnClickListener {
            dialog.dismiss()
            onQuit()
        }

        dialog.show()
    }

    fun showTimeUpDialog(
        onBuyMoreTime: () -> Unit,
        onRestartLevel: () -> Unit,
    ) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)

        // Set the window background to transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Inflate the dialog layout using View Binding
        val binding = DialogTimeUpBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        // Set up button click listeners
        binding.btnBuyMoreTime.setOnClickListener {
            onBuyMoreTime()
            dialog.dismiss()
        }

        binding.btnRestartLevel.setOnClickListener {
            onRestartLevel()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showLevelCompleteDialog(
        level: Int,
        onStartNextLevel: () -> Unit,
    ) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)

        // Set the window background to transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Inflate the custom layout using View Binding
        val binding = DialogLevelCompleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        // Set dialog text
        binding.tvTitle.text = "Level Complete!"
        binding.tvMessage.text = "Awesome!"
        binding.btnStartNextLevel.text = "Start Level $level"

        // Handle button click
        binding.btnStartNextLevel.setOnClickListener {
            onStartNextLevel()
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun showQuestionCountDialog(onSetUpQuestionCount: (Int) -> Unit) {
        val options = arrayOf("2", "3", "4")
        AlertDialog
            .Builder(context)
            .setTitle("Choose Number of Questions")
            .setItems(options) { _, which ->
                val count = options[which].toInt()
                onSetUpQuestionCount(count)
            }.show()
    }
}
