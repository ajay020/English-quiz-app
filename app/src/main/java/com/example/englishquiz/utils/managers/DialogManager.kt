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
    DARK,
    NATURE,
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
            onDarkModeChanged: (Boolean) -> Unit,
        ): Dialog {
            val dialog = Dialog(context, R.style.FullWidthDialog)

            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setDimAmount(0.5f)

            val binding = DialogSettingsBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            val switchSound = binding.switchSound
            val switchMusic = binding.switchMusic
            val switchDarkTheme = binding.switchDarkTheme
            val btnClose = binding.btnCloseSettings

            switchSound.isChecked = preferenceManager.isSoundEnabled()
            switchMusic.isChecked = preferenceManager.isMusicEnabled()
            switchDarkTheme.isChecked = preferenceManager.isDarkModeEnabled()

            switchSound.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setSoundEnabled(isChecked)
                onAudioChanged(isChecked)
            }

            switchMusic.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setMusicEnabled(isChecked)
                onMusicChanged(isChecked)
            }

            switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.saveThemePreference(isChecked)
                onDarkModeChanged(isChecked)
            }

            btnClose.setOnClickListener {
                soundManager.playButtonClickSound()
                dialog.dismiss()
            }

            dialog.show()
            return dialog
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
