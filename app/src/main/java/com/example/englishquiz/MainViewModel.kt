package com.example.englishquiz

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {
    val preferenceManager = PreferenceManager(application)

    private var mediaPlayer: MediaPlayer? = null
    private val _isMusicEnabled = MutableLiveData(true)
    val isMusicEnabled: LiveData<Boolean> = _isMusicEnabled

    private val soundPool: SoundPool
    private var clickSoundId: Int = 0
    private var toggleOffOnSound: Int = 0

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

        // Load sound resource
        clickSoundId = soundPool.load(application, R.raw.mouse_click, 1)
        toggleOffOnSound = soundPool.load(application, R.raw.switch_on_off_sound, 1)
    }

    fun playClickSound() {
        // Play the sound with default volume and no looping
        soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playToggleOffOnSound() {
        // Play the sound with default volume and no looping
        soundPool.play(toggleOffOnSound, 1f, 1f, 1, 0, 1f)
    }

    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        if (enabled) {
            startMusic()
        } else {
            stopMusic()
        }
    }

    fun startMusic() {
        if (mediaPlayer == null) {
            val context = getApplication<Application>()
            mediaPlayer =
                MediaPlayer.create(context, R.raw.bg_music).apply {
                    isLooping = true
                    start()
                }
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopMusic() // Cleanup when ViewModel is destroyed
        soundPool.release() // Release SoundPool resources
    }
}
