package com.example.englishquiz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.databinding.ActivityMainBinding
import com.example.englishquiz.utils.managers.DialogManager
import com.example.englishquiz.utils.managers.SoundManager
import com.example.englishquiz.viewmodel.MainViewModel
import com.example.englishquiz.views.StreakTrackerView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding

    // Hilt will automatically provide the ViewModel with injected dependencies
    val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var streakTracker: StreakTrackerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start music if enabled
        if (viewModel.isMusicEnabled.value == true) {
            viewModel.startMusic()
        }
        setUpViews()
    }

    private fun setUpViews() {
        val recyclerView: RecyclerView = binding.streakRecyclerView
        streakTracker.setupStreakTracker(recyclerView)

        // Set up coin display
        val coinDisplay = binding.coinDisplay
        coinDisplay.setCoinCount(viewModel.getCoins())

        // Set up settings button
        binding.btnSettings.setOnClickListener {
            viewModel.playClickSound()
            dialogManager.showSettingsDialog(
                onAudioChanged = { isSoundEnabled ->
                    // Handle sound setting change
                },
                onMusicChanged = { isMusicEnabled ->
                    viewModel.setMusicEnabled(isMusicEnabled)
                },
                onThemeSelected = { selectedTheme ->
                    recreate() // Recreate activity to apply theme
                },
            )
        }

        // Set up start quiz button
        binding.btnStartQuiz.setOnClickListener {
            viewModel.playClickSound()
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh streak data on resumption
        streakTracker.refreshStreakData()

        // Update the coin display
        binding.coinDisplay.setCoinCount(viewModel.getCoins())
    }
}
