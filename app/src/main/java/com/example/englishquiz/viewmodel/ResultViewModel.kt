package com.example.englishquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel
    @Inject
    constructor(
        private val questionRepository: QuestionRepository,
        private val preferenceManager: PreferenceManager,
    ) : ViewModel() {
        private val _questionCount = MutableStateFlow(0)
        val questionCount = _questionCount.asStateFlow()

        private val _daysBetweenLaunchAndCompletion = MutableStateFlow(0)
        val daysBetweenLaunchAndCompletion: StateFlow<Int> = _daysBetweenLaunchAndCompletion.asStateFlow()

        init {
            loadQuestionCount()
            _daysBetweenLaunchAndCompletion.value = preferenceManager.getDaysBetweenLaunchAndCompletion()
        }

        private fun loadQuestionCount() {
            viewModelScope.launch {
                questionRepository.getQuestionCount().collectLatest { count ->
                    _questionCount.value = count
                }
            }
        }

        fun resetQuestions() {
            viewModelScope.launch {
                questionRepository.resetAllQuestions()
                preferenceManager.saveCurrentLevel(1)
            }
        }
    }
