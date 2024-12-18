package com.example.englishquiz.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.managers.SoundManager
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    // Use this rule to execute LiveData updates instantly (synchronously)
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocks for dependencies
    private lateinit var soundManager: SoundManager
    private lateinit var preferenceManager: PreferenceManager

    // Class under test
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        soundManager = mockk(relaxed = true) // Relaxed mock for SoundManager
        preferenceManager = mockk(relaxed = true) // Relaxed mock for PreferenceManager

        // Default mock behavior for preferenceManager.isMusicEnabled()
        every { preferenceManager.isMusicEnabled() } returns true

        viewModel = MainViewModel(soundManager, preferenceManager)
    }

    @Test
    fun `init sets isMusicEnabled based on preferenceManager`() {
        // Verify initial LiveData value
        assertEquals(true, viewModel.isMusicEnabled.value)

        // Verify that preferenceManager.isMusicEnabled() was called
        verify { preferenceManager.isMusicEnabled() }
    }

    @Test
    fun `playClickSound calls soundManager playButtonClickSound`() {
        // Call the method
        viewModel.playClickSound()

        // Verify that the soundManager's playButtonClickSound was called
        verify { soundManager.playButtonClickSound() }
    }

    @Test
    fun `startMusic starts music when isMusicEnabled is true`() {
        // Call the method
        viewModel.startMusic()

        // Verify that soundManager.startMusic() is called
        verify { soundManager.startMusic() }
    }

    @Test
    fun `startMusic does nothing when isMusicEnabled is false`() {
        // Set isMusicEnabled to false
        viewModel.setMusicEnabled(false)

        // Clear any prior interactions
        clearMocks(soundManager)

        // Call the method
        viewModel.startMusic()

        // Verify that soundManager.startMusic() was NOT called
        verify(exactly = 0) { soundManager.startMusic() }
    }

    @Test
    fun `setMusicEnabled updates isMusicEnabled and starts or stops music`() {
        // Observe LiveData changes
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isMusicEnabled.observeForever(observer)

        // Enable music
        viewModel.setMusicEnabled(true)
        assertEquals(true, viewModel.isMusicEnabled.value)
        verify { soundManager.startMusic() }

        // Disable music
        viewModel.setMusicEnabled(false)
        assertEquals(false, viewModel.isMusicEnabled.value)
        verify { soundManager.stopMusic() }

        // Remove observer
        viewModel.isMusicEnabled.removeObserver(observer)
    }

    @Test
    fun `onCleared calls soundManager release`() {
        // Call the method
        viewModel.onCleared()

        // Verify that soundManager.release() was called
        verify { soundManager.release() }
    }

    @Test
    fun `getCoins returns value from preferenceManager`() {
        // Mock return value
        every { preferenceManager.getCoins() } returns 100

        // Call the method and assert the result
        val coins = viewModel.getCoins()
        assertEquals(100, coins)

        // Verify that preferenceManager.getCoins() was called
        verify { preferenceManager.getCoins() }
    }
}
