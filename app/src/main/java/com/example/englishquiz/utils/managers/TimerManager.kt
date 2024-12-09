package com.example.englishquiz.utils.managers

import android.os.CountDownTimer

class TimerManager(
    private var timerDuration: Long = 25000L,
) {
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var onTick: ((Long) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null

    fun startTimer(
        duration: Long? = null,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit,
    ) {
        this.onTick = onTick // Store callbacks
        this.onFinish = onFinish

        // Use provided duration if available, else use the default or remaining time
        timeLeftInMillis = duration ?: timerDuration

        countDownTimer?.cancel()

        countDownTimer =
            object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    onTick(millisUntilFinished / 1000) // Convert to seconds
                }

                override fun onFinish() {
                    onFinish()
                }
            }.start()
    }

    fun addTimeDuration(extraTimeInMillis: Long) {
        countDownTimer?.cancel() // Cancel existing timer
        timeLeftInMillis += extraTimeInMillis // Update remaining time

        countDownTimer =
            object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    this@TimerManager.onTick?.invoke(millisUntilFinished / 1000) // Invoke tick callback
                }

                override fun onFinish() {
                    this@TimerManager.onFinish?.invoke() // Invoke finish callback
                }
            }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
    }

    fun getTimeLeft() = timeLeftInMillis

    fun resetTimer() {
        stopTimer() // Stop the timer
        timeLeftInMillis = timerDuration // Reset to the initial duration
    }
}
