package com.example.englishquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val soundManager = SoundManager(application)
    private val _isMusicEnabled = MutableLiveData(true)
    val isMusicEnabled: LiveData<Boolean> = _isMusicEnabled

    fun playClickSound() {
        soundManager.playButtonClickSound()
    }

    fun playToggleOffOnSound() {
        soundManager.playToggleOffOnSound()
    }

    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        if (enabled) {
            soundManager.startMusic()
        } else {
            soundManager.stopMusic()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
