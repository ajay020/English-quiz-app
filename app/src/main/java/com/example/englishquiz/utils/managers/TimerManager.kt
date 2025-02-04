package com.example.englishquiz.utils.managers

import android.os.CountDownTimer

class TimerManager(
    private var timerDuration: Long = 25000L,
) {
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var onTick: ((Long, Float) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null

    fun startTimer(
        duration: Long? = null,
        onTick: (Long, Float) -> Unit,
        onFinish: () -> Unit,
    ) {
        this.onTick = onTick // Store callbacks
        this.onFinish = onFinish

        // Use provided duration if available, else use the default or remaining time
        timeLeftInMillis = duration ?: timerDuration

        countDownTimer?.cancel()

        countDownTimer =
            object : CountDownTimer(timeLeftInMillis, 16L) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    val secondsLeft = (millisUntilFinished / 1000)

                    val progress = (millisUntilFinished.toFloat() / timerDuration.toFloat())
                    onTick(secondsLeft, progress) // Convert to seconds
                }

                override fun onFinish() {
                    onFinish()
                }
            }.start()
    }

    fun addTimeDuration(extraTimeInMillis: Long) {
        countDownTimer?.cancel() // Cancel existing timer
        timeLeftInMillis += extraTimeInMillis // Update remaining time
        val totalTimeDuration = timeLeftInMillis

        countDownTimer =
            object : CountDownTimer(timeLeftInMillis, 16L) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    val secondsLeft = (millisUntilFinished / 1000)
                    val progress = (millisUntilFinished.toFloat() / totalTimeDuration.toFloat())

                    this@TimerManager.onTick?.invoke(secondsLeft, progress) // Invoke tick callback
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
}
