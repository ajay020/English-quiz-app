package com.example.englishquiz.utils

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.englishquiz.data.preferences.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppTimeTrackerObserver(
    private val preferenceManager: PreferenceManager,
) : DefaultLifecycleObserver {
    private var appStartTime: Long = 0L
    private var timerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Persistent scope

    // Called when the app comes to the foreground
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        appStartTime = System.currentTimeMillis()
        startPeriodicTimeSaving()
//        Log.d("AppTimeTracker", "App entered foreground. Start time: $appStartTime")
    }

    // Called when the app goes into the background
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopPeriodicTimeSaving()
        saveTimeSpent()
    }

    // Start periodic saving every minute
    private fun startPeriodicTimeSaving() {
        if (timerJob == null) { // Prevent multiple jobs
            timerJob =
                coroutineScope.launch {
                    while (isActive) {
                        delay(60 * 1000) // Save every 1 minute
                        saveTimeSpent()
                        Log.d("AppTimeTracker", "Periodic time saved")
                    }
                }
        }
    }

    // Stop the periodic saving job
    private fun stopPeriodicTimeSaving() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun saveTimeSpent() {
        if (appStartTime == 0L) return // Ensure valid start time

        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - appStartTime
        val totalTimeSpent = preferenceManager.getTotalTimeSpent() + sessionDuration

        preferenceManager.saveTotalTimeSpent(totalTimeSpent)
        appStartTime = currentTime // Reset start time for next session

//        Log.d(
//            "AppTimeTracker",
//            "Session saved. Duration: $sessionDuration ms, Total time: $totalTimeSpent ms",
//        )
    }
}
