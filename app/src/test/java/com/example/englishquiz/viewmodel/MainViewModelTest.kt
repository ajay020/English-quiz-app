package com.example.englishquiz.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.managers.SoundManager
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    // Executes each task synchronously
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel
    private val soundManager: SoundManager = mockk(relaxed = true)
    private val preferenceManager: PreferenceManager = mockk(relaxed = true)

    @Before
    fun setup() {
        every { preferenceManager.isMusicEnabled() } returns true
        every { preferenceManager.getCoins() } returns 100

        viewModel = MainViewModel(soundManager, preferenceManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test initial music enabled state is fetched from preference manager`() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isMusicEnabled.observeForever(observer)

        verify { preferenceManager.isMusicEnabled() }
        verify { observer.onChanged(true) }
    }

    @Test
    fun `test playClickSound calls soundManager playButtonClickSound`() {
        viewModel.playClickSound()
        verify { soundManager.playButtonClickSound() }
    }

    @Test
    fun `test startMusic calls soundManager startMusic when enabled`() {
        viewModel.startMusic()
        verify { soundManager.startMusic() }
    }

    @Test
    fun `test setMusicEnabled starts music when enabled`() {
        viewModel.setMusicEnabled(true)
        verify { soundManager.startMusic() }
    }

    @Test
    fun `test setMusicEnabled stops music when disabled`() {
        viewModel.setMusicEnabled(false)
        verify { soundManager.stopMusic() }
    }

    @Test
    fun `test getCoins returns correct value`() {
        val coins = viewModel.getCoins()
        Assert.assertEquals(100, coins)
        verify { preferenceManager.getCoins() }
    }

    @Test
    fun `test onCleared releases sound manager`() {
        viewModel.onCleared()
        verify { soundManager.release() }
    }
}
