package com.example.englishquiz.utils.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.englishquiz.R
import com.example.englishquiz.data.preferences.PreferenceManager

class SoundManager(
    private val preferenceManager: PreferenceManager,
    private val context: Context,
) {
    // Background Music Player
    private var mediaPlayer: MediaPlayer? = null

    // SoundPool for short sound effects
    private val soundPool: SoundPool

    // Sound effect IDs
    private var clickSoundId: Int = 0
    private var toggleOffOnSoundId: Int = 0
    private var correctAnswerSoundId: Int = 0
    private var incorrectAnswerSoundId: Int = 0
    private var buttonClickSoundId: Int = 0
    private var levelCompleteSoundId: Int = 0

    init {
        // Set up SoundPool with appropriate audio attributes
        val audioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        soundPool =
            SoundPool
                .Builder()
                .setMaxStreams(5) // Max simultaneous sounds
                .setAudioAttributes(audioAttributes)
                .build()

        // Load sound effects
        loadSoundEffects()
    }

    private fun loadSoundEffects() {
        clickSoundId = soundPool.load(context, R.raw.mouse_click, 1)
        toggleOffOnSoundId = soundPool.load(context, R.raw.switch_on_off_sound, 1)
        correctAnswerSoundId = soundPool.load(context, R.raw.correct_answer, 1)
        incorrectAnswerSoundId = soundPool.load(context, R.raw.incorrect_answer, 1)
        buttonClickSoundId = soundPool.load(context, R.raw.button_click, 1)
        levelCompleteSoundId = soundPool.load(context, R.raw.level_complete, 1)
    }

    // Play click sound
    fun playClickSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    // Play toggle sound
    fun playToggleOffOnSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(toggleOffOnSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    // Play sound effects
    fun playCorrectAnswerSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(correctAnswerSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playIncorrectAnswerSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(incorrectAnswerSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playButtonClickSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(buttonClickSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playLevelCompleteSound() {
        if (preferenceManager.isSoundEnabled()) {
            soundPool.play(levelCompleteSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    // Start background music
    fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer =
                MediaPlayer.create(context, R.raw.bg_music).apply {
                    isLooping = true
                    start()
                }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start() // Resume playback if the media player exists but isn't playing
        }
    }

    // Stop background music
    fun stopMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause() // Pause instead of stopping to allow resuming
        }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Release resources
    fun release() {
        soundPool.release()
        stopMusic()
    }
}
