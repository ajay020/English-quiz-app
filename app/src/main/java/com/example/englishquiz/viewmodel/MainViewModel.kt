package com.example.englishquiz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.managers.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
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
