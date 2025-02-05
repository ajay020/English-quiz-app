package com.example.quizzybee.utils.managers

import org.junit.Before

class TimerManagerTest {
    private lateinit var timerManager: TimerManager
    private val defaultDuration = 25000L

    @Before
    fun setup() {
        timerManager = TimerManager(defaultDuration)
    }

//    @Test
//    fun `startTimer initializes timer with default duration`() {
//        var tickCalled = false
//        var finishedCalled = false
//
//        timerManager.startTimer(
//            onTick = { timeLeft ->
//                tickCalled = true
//                assertEquals(defaultDuration / 1000, timeLeft + 1) // Initial tick value in seconds
//            },
//            onFinish = { finishedCalled = true },
//        )
//
//        Thread.sleep(1100) // Simulate 1 second passing
//        assert(tickCalled)
//        assert(!finishedCalled)
//    }

//    @Test
//    fun `startTimer with custom duration overrides default`() {
//        val customDuration = 15000L
//        var tickCalled = false
//
//        timerManager.startTimer(
//            duration = customDuration,
//            onTick = { timeLeft ->
//                tickCalled = true
//                assertEquals(customDuration / 1000, timeLeft + 1)
//            },
//            onFinish = {},
//        )
//
//        Thread.sleep(1100)
//        assert(tickCalled)
//    }
//
//    @Test
//    fun `addTimeDuration extends the timer duration`() {
//        val extraTime = 5000L
//
//        timerManager.startTimer(
//            onTick = {},
//            onFinish = {},
//        )
//
//        timerManager.addTimeDuration(extraTime)
//
//        assertEquals(defaultDuration + extraTime, timerManager.getTimeLeft())
//    }
//
//    @Test
//    fun `stopTimer cancels the timer`() {
//        timerManager.startTimer(
//            onTick = {},
//            onFinish = {},
//        )
//
//        timerManager.stopTimer()
//
//        assertEquals(0L, timerManager.getTimeLeft())
//    }
//
//    @Test
//    fun `resetTimer stops and resets the timer`() {
//        timerManager.startTimer(
//            onTick = {},
//            onFinish = {},
//        )
//
//        timerManager.resetTimer()
//
//        assertEquals(defaultDuration, timerManager.getTimeLeft())
//    }
}
