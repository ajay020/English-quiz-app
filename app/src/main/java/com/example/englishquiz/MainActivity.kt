package com.example.englishquiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.databinding.ActivityMainBinding
import com.example.englishquiz.utils.QuestionLoadingScript

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogManager: DialogManager
    private val viewModel: MainViewModel by viewModels()
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

        // Initialize StreakTrackerView
        streakTracker = StreakTrackerView(this)
        val recyclerView: RecyclerView = binding.streakRecyclerView
        streakTracker.setupStreakTracker(recyclerView)

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

        binding.btnStartQuiz.setOnClickListener {
            viewModel.playClickSound()
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        streakTracker.refreshStreakData() // Refresh streak data on resumption
    }
}
