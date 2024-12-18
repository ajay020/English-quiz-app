package com.example.englishquiz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.core.QuizApplication
import com.example.englishquiz.databinding.ActivityMainBinding
import com.example.englishquiz.utils.QuestionLoadingScript
import com.example.englishquiz.utils.managers.DialogManager
import com.example.englishquiz.viewmodel.MainViewModel
import com.example.englishquiz.viewmodel.MainViewModelFactory
import com.example.englishquiz.views.StreakTrackerView

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogManager: DialogManager
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            (application as QuizApplication).soundManager,
            (application as QuizApplication).preferenceManager,
        )
    }
    private lateinit var streakTracker: StreakTrackerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Load questions into database
        val database = (application as QuizApplication).database
        QuestionLoadingScript.importQuestionsFromJson(this, database)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogManager = DialogManager(this)

        // Start music if enabled
        if (viewModel.isMusicEnabled.value == true) {
            viewModel.startMusic()
        }

        setUpViews()
    }

    private fun setUpViews() {
        // Initialize StreakTrackerView
        streakTracker = StreakTrackerView(this)
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
