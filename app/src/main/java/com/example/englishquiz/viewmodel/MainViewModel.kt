package com.example.englishquiz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.managers.SoundManager

class MainViewModel(
    private val soundManager: SoundManager,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {
    private val _isMusicEnabled = MutableLiveData(true)
    val isMusicEnabled: LiveData<Boolean> = _isMusicEnabled

    init {
        _isMusicEnabled.value = preferenceManager.isMusicEnabled()
    }

    fun playClickSound() {
        soundManager.playButtonClickSound()
    }

    fun startMusic() {
        if (_isMusicEnabled.value == true) {
            soundManager.startMusic()
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled

        if (enabled) {
            soundManager.startMusic()
        } else {
            soundManager.stopMusic()
        }
    }

    public override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    fun getCoins(): Int = preferenceManager.getCoins()
}

class MainViewModelFactory(
    private val soundManager: SoundManager,
    private val preferenceManager: PreferenceManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(soundManager, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
