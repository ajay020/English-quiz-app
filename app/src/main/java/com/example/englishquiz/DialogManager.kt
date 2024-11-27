package com.example.englishquiz

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.englishquiz.databinding.DialogLevelCompleteBinding
import com.example.englishquiz.databinding.DialogPauseBinding
import com.example.englishquiz.databinding.DialogSettingsBinding
import com.example.englishquiz.databinding.DialogTimeUpBinding

enum class Theme {
    CLASSIC,
    DARK,
    NATURE,
    OCEAN,
}

class DialogManager(
    private val context: Context,
) {
    fun showSettingsDialog(
        onAudioChanged: (Boolean) -> Unit,
        onMusicChanged: (Boolean) -> Unit,
        onThemeSelected: (Theme) -> Unit,
    ) {
        val preferenceManager = PreferenceManager(context)
        val theme = preferenceManager.getSelectedThemeFromPreferences()
        val dialog = Dialog(context, R.style.FullWidthDialog)

        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.5f)

        val binding = DialogSettingsBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        val soundButton = binding.buttonSound
        val musicButton = binding.buttonMusic

        val themeClassic = binding.themeClassic
        val themeDark = binding.themeDark
        val themeNature = binding.themeNature
        val themeOcean = binding.themeOcean

        val selectedTheme = preferenceManager.getSelectedThemeFromPreferences()

        // Map themes to their respective views
        val themeViews =
            mapOf(
                Theme.CLASSIC to Pair(binding.themeClassic, binding.checkmarkClassic),
                Theme.DARK to Pair(binding.themeDark, binding.checkmarkDark),
                Theme.NATURE to Pair(binding.themeNature, binding.checkmarkNature),
                Theme.OCEAN to Pair(binding.themeOcean, binding.checkmarkOcean),
            )

        // Add checkmark to the currently selected theme
        // Update UI for the selected theme
        fun updateSelectedTheme(theme: Theme) {
            themeViews.forEach { (key, value) ->
                val checkmarkView = value.second // Access the checkmark directly
                checkmarkView.visibility = if (key == theme) View.VISIBLE else View.GONE
            }
        }

        // Initialize the selected theme
        updateSelectedTheme(selectedTheme)

        // Set up click listeners for themes
        themeViews.forEach { (theme, value) ->
            val themeView = value.first // Access the theme view directly
            themeView.setOnClickListener {
                onThemeSelected(theme)
                updateSelectedTheme(theme)
                preferenceManager.saveSelectedThemeToPreferences(theme)
            }
        }

        // Initially, assume both sound and music are enabled
        var isSoundEnabled = true
        var isMusicEnabled = true

        soundButton.setOnClickListener {
            isSoundEnabled = !isSoundEnabled
            soundButton.setImageResource(if (isSoundEnabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
            onAudioChanged(isSoundEnabled)
        }

        musicButton.setOnClickListener {
            isMusicEnabled = !isMusicEnabled
            musicButton.setImageResource(if (isMusicEnabled) R.drawable.ic_music_on else R.drawable.ic_music_off)
            onMusicChanged(isMusicEnabled)
        }

        // ------------------------- Theme controls -------------------------------

        themeClassic.setOnClickListener {
            onThemeSelected(Theme.CLASSIC)
            updateSelectedTheme(Theme.CLASSIC)
            preferenceManager.saveSelectedThemeToPreferences(Theme.CLASSIC)
        }

        themeDark.setOnClickListener {
            onThemeSelected(Theme.DARK)
            updateSelectedTheme(Theme.DARK)
            preferenceManager.saveSelectedThemeToPreferences(Theme.DARK) // Save the selection
        }

        themeNature.setOnClickListener {
            onThemeSelected(Theme.NATURE)
            updateSelectedTheme(Theme.NATURE)
            preferenceManager.saveSelectedThemeToPreferences(Theme.NATURE) // Save the selection
        }

        themeOcean.setOnClickListener {
            onThemeSelected(Theme.OCEAN)
            updateSelectedTheme(Theme.OCEAN)
            preferenceManager.saveSelectedThemeToPreferences(Theme.OCEAN) // Save the selection
        }

        val closeButton = binding.buttonCloseSettings

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showPauseDialog(
        onResume: () -> Unit,
        onQuit: () -> Unit,
    ) {
        val dialog = Dialog(context, R.style.FullWidthDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false) // Disable dismissing by tapping outside

        val binding = DialogPauseBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        // Find views in the custom dialog layout
        val btnResume = binding.btnResume
        val btnQuit = binding.btnQuit

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
        val dialog = Dialog(context, R.style.FullWidthDialog)

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
        val dialog = Dialog(context, R.style.FullWidthDialog)

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
