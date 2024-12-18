package com.example.englishquiz.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule

class QuizViewModelTest {
    // Use this rule to execute LiveData updates instantly (synchronously)
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
    }
}
