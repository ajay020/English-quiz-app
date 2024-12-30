package com.example.englishquiz.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RespositoryDemo(
    val dispatcher: CoroutineDispatcher,
) {
    var count = 0

    suspend fun getUser(): String {
        delay(5000)
        return "John Doe"
    }

    suspend fun setCount() {
        CoroutineScope(dispatcher).launch {
            count = 1
        }
    }
}
