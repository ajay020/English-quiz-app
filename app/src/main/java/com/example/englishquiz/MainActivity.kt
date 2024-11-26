package com.example.englishquiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.englishquiz.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogManager: DialogManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogManager = DialogManager(this)

        // Observe music state to start or stop music
        viewModel.isMusicEnabled.observe(this) { isMusicEnabled ->
            if (isMusicEnabled) {
                viewModel.startMusic()
            } else {
                viewModel.stopMusic()
            }
        }

        binding.btnSettings.setOnClickListener {
            viewModel.playClickSound()
            dialogManager.showSettingsDialog(
                onAudioChanged = { isSoundEnabled ->
                    // Handle sound setting change
                },
                onMusicChanged = { isMusicEnabled ->
                    viewModel.playToggleOffOnSound()
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

        // Start music on app start
        viewModel.setMusicEnabled(true)
    }
}
