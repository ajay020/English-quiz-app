package com.example.englishquiz.utils.managers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.englishquiz.R
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.databinding.DialogLevelCompleteBinding
import com.example.englishquiz.databinding.DialogPauseBinding
import com.example.englishquiz.databinding.DialogSettingsBinding
import com.example.englishquiz.databinding.DialogTimeUpBinding
import com.example.englishquiz.utils.extensions.animateNumberChange
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class Theme {
    CLASSIC,
    DARK,
    NATURE,
    OCEAN,
}

class DialogManager
    @Inject
    constructor(
        private val context: Context,
        private val soundManager: SoundManager,
        private val preferenceManager: PreferenceManager,
    ) {
        companion object {
            const val WEEK_STREAK_REWARD = 50
        }

        fun showSettingsDialog(
            onAudioChanged: (Boolean) -> Unit,
            onMusicChanged: (Boolean) -> Unit,
            onThemeSelected: (Theme) -> Unit,
        ) {
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
            var isSoundEnabled = preferenceManager.isSoundEnabled()
            var isMusicEnabled = preferenceManager.isMusicEnabled()
            soundButton.setImageResource(if (isSoundEnabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
            musicButton.setImageResource(if (isMusicEnabled) R.drawable.ic_music_on else R.drawable.ic_music_off)

            soundButton.setOnClickListener {
                soundManager.playButtonClickSound()
                isSoundEnabled = !isSoundEnabled
                soundButton.setImageResource(if (isSoundEnabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
                onAudioChanged(isSoundEnabled)
                preferenceManager.setSoundEnabled(isSoundEnabled)
            }

            musicButton.setOnClickListener {
                soundManager.playButtonClickSound()
                isMusicEnabled = !isMusicEnabled
                musicButton.setImageResource(if (isMusicEnabled) R.drawable.ic_music_on else R.drawable.ic_music_off)
                onMusicChanged(isMusicEnabled)
                preferenceManager.setMusicEnabled(isMusicEnabled)
            }

            // ------------------------- Theme controls -------------------------------

            themeClassic.setOnClickListener {
                soundManager.playButtonClickSound()

                onThemeSelected(Theme.CLASSIC)
                updateSelectedTheme(Theme.CLASSIC)
                preferenceManager.saveSelectedThemeToPreferences(Theme.CLASSIC)
                dialog.dismiss()
            }

            themeDark.setOnClickListener {
                soundManager.playButtonClickSound()

                onThemeSelected(Theme.DARK)
                updateSelectedTheme(Theme.DARK)
                preferenceManager.saveSelectedThemeToPreferences(Theme.DARK) // Save the selection
                dialog.dismiss()
            }

            themeNature.setOnClickListener {
                soundManager.playButtonClickSound()

                onThemeSelected(Theme.NATURE)
                updateSelectedTheme(Theme.NATURE)
                preferenceManager.saveSelectedThemeToPreferences(Theme.NATURE) // Save the selection
                dialog.dismiss()
            }

            themeOcean.setOnClickListener {
                soundManager.playButtonClickSound()

                onThemeSelected(Theme.OCEAN)
                updateSelectedTheme(Theme.OCEAN)
                preferenceManager.saveSelectedThemeToPreferences(Theme.OCEAN) // Save the selection

                dialog.dismiss()
            }

            val closeButton = binding.buttonCloseSettings

            closeButton.setOnClickListener {
                soundManager.playButtonClickSound()
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
                soundManager.playButtonClickSound()
            }

            btnQuit.setOnClickListener {
                soundManager.playButtonClickSound()
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
                soundManager.playButtonClickSound()

                onBuyMoreTime()
                dialog.dismiss()
            }

            binding.btnRestartLevel.setOnClickListener {
                soundManager.playButtonClickSound()

                onRestartLevel()
                dialog.dismiss()
            }
            dialog.show()
        }

        fun showLevelCompleteDialog(
            level: Int,
            isStreakCompleted: Boolean,
            onStartNextLevel: () -> Unit,
            onCoinsUpdated: (Int) -> Unit,
        ) {
            val dialog = Dialog(context)
            dialog.setCancelable(false)

            // Set the window background to transparent
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Inflate the custom layout using View Binding
            val binding = DialogLevelCompleteBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            dialog.window?.let { window ->
                val layoutParams = window.attributes
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                window.attributes = layoutParams
            }

            val konfettiView: KonfettiView = binding.konfettiView
            konfettiView.start(
                Party(
                    speed = 0f,
                    maxSpeed = 50f,
                    damping = 0.9f,
                    spread = 360,
                    colors =
                        listOf(
                            Color.YELLOW,
                            Color.GREEN,
                            Color.MAGENTA,
                            Color.BLUE,
                            Color.RED,
                            Color.CYAN,
                            Color.BLUE,
                        ),
                    emitter = Emitter(duration = 5, TimeUnit.SECONDS).max(500).perSecond(100),
                    position = Position.Relative(0.5, 0.5), // Center of the screen
                ),
            )

            // Set dialog text
            binding.btnStartNextLevel.text = "Start Level $level"

            val preferenceManager = PreferenceManager(context)
            binding.tvCoins.animateNumberChange(
                startValue = 0,
                endValue = preferenceManager.getCoins(),
                duration = 700L,
                prefix = "Coins ",
            )

            // Show streak reward if streak is completed
            if (isStreakCompleted) {
                val currentCoins = preferenceManager.getCoins()
                val updatedCoins = currentCoins + WEEK_STREAK_REWARD

                binding.tvCoins.animateNumberChange(
                    startValue = currentCoins,
                    endValue = updatedCoins,
                    duration = 1000L,
                    prefix = "Coins",
                )
                binding.streakRewardSection.visibility = View.VISIBLE

                // Reset streak
                val streakManager = StreakManager(preferenceManager)
                streakManager.resetStreak()
                onCoinsUpdated(WEEK_STREAK_REWARD)
            } else {
                binding.streakRewardSection.visibility = View.GONE
            }

            // Handle button click
            binding.btnStartNextLevel.setOnClickListener {
                soundManager.playButtonClickSound()

                onStartNextLevel()
                dialog.dismiss()
            }

            // Show the dialog
            dialog.show()
        }
    }
