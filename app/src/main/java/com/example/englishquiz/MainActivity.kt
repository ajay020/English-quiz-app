package com.example.englishquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.databinding.ActivityMainBinding
import com.example.englishquiz.utils.QuestionLoadingScript
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

//         test adding streak dates
//        val daysToAdd = listOf(0, 1, 5, 6)
//        val startDate = "2024-11-25"
//        val streakManager = StreakManager(preferenceManager)
//        testAddDatesToStreak(streakManager, startDate, daysToAdd)
    }

    override fun onResume() {
        super.onResume()
        streakTracker.refreshStreakData() // Refresh streak data on resumption
    }

    // Demonstration of streak tracking
    private fun testAddDatesToStreak(
        streakManager: StreakManager,
        startDate: String,
        daysToAdd: List<Int>,
    ) {
        // Parse the start date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startCalendar =
            Calendar.getInstance().apply {
                time = dateFormat.parse(startDate) ?: throw IllegalArgumentException("Invalid start date format")
            }

        // Add specified days to the streak
        daysToAdd.forEach { dayOffset ->
            val testCalendar = startCalendar.clone() as Calendar
            testCalendar.add(Calendar.DAY_OF_YEAR, dayOffset)
            val testDate = dateFormat.format(testCalendar.time)

            Log.d("MAIN ACTIVITY", "Adding date to streak: $testDate")
            streakManager.addCompletedDat(testDate)
        }

        // Display the current streak data
        val currentStreak = preferenceManager.getStreakDates()
        Log.d("MAIN ACTIVITY", "Current streak data: $currentStreak")
    }
}
