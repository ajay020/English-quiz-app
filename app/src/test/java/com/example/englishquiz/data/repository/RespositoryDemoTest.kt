package com.example.englishquiz.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RespositoryDemoTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = RespositoryDemo(mainDispatcherRule.dispatcher)

    @Test
    fun test_getUser_returns_user_name() {
        runTest {
            val userName = repository.getUser()
            assert(userName == "John Doe")
        }
    }

    @Test
    fun test_setCount_increments_count() {
        runTest {
            repository.setCount()
            mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assert(repository.count == 1)
        }
    }
}
