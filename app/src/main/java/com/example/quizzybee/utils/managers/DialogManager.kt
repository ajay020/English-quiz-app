package com.example.quizzybee.utils.managers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.example.quizzybee.R
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.databinding.DialogLevelCompleteBinding
import com.example.quizzybee.databinding.DialogPauseBinding
import com.example.quizzybee.databinding.DialogSettingsBinding
import com.example.quizzybee.databinding.DialogTimeUpBinding
import com.example.quizzybee.notification.NotificationScheduler
import com.example.quizzybee.utils.TimePickerUtil
import com.example.quizzybee.utils.extensions.animateNumberChange
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.Locale
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
            notificationScheduler: NotificationScheduler,
            onAudioChanged: (Boolean) -> Unit,
            onMusicChanged: (Boolean) -> Unit,
            onDarkModeChanged: (Boolean) -> Unit,
            onNotificationChanged: (Boolean) -> Unit,
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
            val switchNotification = binding.switchNotifications
            val layoutTimePicker: LinearLayout = binding.layoutTimePicker

            val btnClose = binding.btnCloseSettings

            switchSound.isChecked = preferenceManager.isSoundEnabled()
            switchMusic.isChecked = preferenceManager.isMusicEnabled()
            switchDarkTheme.isChecked = preferenceManager.isDarkModeEnabled()
            switchNotification.isChecked = preferenceManager.isNotificationEnabled()

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

            switchNotification.setOnCheckedChangeListener { _, isChecked ->
                preferenceManager.setNotificationEnabled(isChecked)
                onNotificationChanged(isChecked)
                layoutTimePicker.visibility = if (isChecked) View.VISIBLE else View.GONE
            }

            // Retrieve selected time
            val timePicker = binding.btnTimepicker
            val (hour, minute) = preferenceManager.getNotificationTime()
            timePicker.text = String.format("%02d:%02d", hour, minute)
            layoutTimePicker.visibility = if (preferenceManager.isNotificationEnabled()) View.VISIBLE else View.GONE

            timePicker.setOnClickListener {
                TimePickerUtil.showTimePickerDialog(context) { hour, minute ->
                    timePicker.text = String.format(Locale("en"), "%02d:%02d", hour, minute)
                    preferenceManager.setNotificationTime(hour, minute)
                    notificationScheduler.scheduleDailyNotification(hour, minute)
                }
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

        fun showQuizCompleteDialog(
            level: Int,
            correctQuestions: Int,
            totalQuestions: Int,
            isStreakCompleted: Boolean,
            onStartNextLevel: () -> Unit,
            onCoinsUpdated: (Int) -> Unit,
            onRetry: () -> Unit,
            saveSolvedQuestions: () -> Unit,
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
            val party =
                Party(
                    speed = 0f,
                    maxSpeed = 50f,
                    damping = 0.9f,
                    spread = 360,
                    colors =
                        listOf(
                            Color.YELLOW,
                            Color.GREEN,
                            Color.BLACK,
                            Color.RED,
                            Color.BLUE,
                        ),
                    emitter = Emitter(duration = 5, TimeUnit.SECONDS).max(200).perSecond(20),
                    position = Position.Relative(0.5, 0.5), // Center of the screen
                )

            val preferenceManager = PreferenceManager(context)
            binding.tvCoins.animateNumberChange(
                startValue = 0,
                endValue = preferenceManager.getCoins(),
                duration = 700L,
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

            // Get Views
            val resultImage = binding.resultImage
            val resultMessage = binding.resultMessage
            val actionButton = binding.actionButton

            // Calculate Percentage
            val percentage = (correctQuestions * 100) / totalQuestions

            // Set Percentage Text
            binding.percentageText.text = context.getString(R.string.score_percentage_text, percentage)

            when {
                correctQuestions == totalQuestions -> {
                    resultImage.setImageResource(R.drawable.bee_amazing)
                    resultMessage.text = context.getString(R.string.perfect_score_message)
                    actionButton.text = context.getString(R.string.next_level, level + 1)
                    // Save next level
                    preferenceManager.saveCurrentLevel(level + 1)
                    // save solved questions
                    saveSolvedQuestions()

                    // display celebration flowers
                    konfettiView.start(party)

                    actionButton.setOnClickListener {
                        dialog.dismiss()
                        onStartNextLevel()
                    }
                }

                (correctQuestions == totalQuestions - 1) && correctQuestions > 0 -> {
                    resultImage.setImageResource(R.drawable.bee_great_job)
                    resultMessage.text = context.getString(R.string.great_job_message)
                    actionButton.text = context.getString(R.string.next_level, level + 1)
                    // Save next level
                    preferenceManager.saveCurrentLevel(level + 1)
                    // save solved questions
                    saveSolvedQuestions()
                    // display celebration flowers
                    konfettiView.start(party)

                    actionButton.setOnClickListener {
                        dialog.dismiss()
                        onStartNextLevel()
                    }
                }

                else -> {
                    resultImage.setImageResource(R.drawable.bee_dont_give_up)
                    resultMessage.text = context.getString(R.string.try_again_message)
                    actionButton.text = context.getString(R.string.retry)

                    actionButton.setOnClickListener {
                        dialog.dismiss()
                        onRetry()
                    }
                }
            }

            // Show the dialog
            dialog.show()
        }
    }
