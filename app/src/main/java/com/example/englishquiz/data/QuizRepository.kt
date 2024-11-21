package com.example.englishquiz.data

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuizRepository(
    private val application: Application,
    private val preferenceStorage: PreferenceStorage,
) {
    fun loadQuestionsFromJson(): List<Question> {
        val jsonString =
            application.assets
                .open("quizzes.json")
                .bufferedReader()
                .use { it.readText() }
        return Gson().fromJson(jsonString, object : TypeToken<List<Question>>() {}.type)
    }

    fun getSolvedQuestions(): Set<Int> = preferenceStorage.getStringSet("solvedQuestions").map { it.toInt() }.toSet()

    fun saveSolvedQuestions(solvedQuestions: Set<Int>) {
        preferenceStorage.putStringSet("solvedQuestions", solvedQuestions.map { it.toString() }.toSet())
    }

    fun getCurrentLevel() = preferenceStorage.getInt("currentLevelNumber", 1)

    fun saveCurrentLevel(level: Int) = preferenceStorage.putInt("currentLevelNumber", level)

    fun getCoins() = preferenceStorage.getInt("coins", 200)

    fun saveCoins(coins: Int) = preferenceStorage.putInt("coins", coins)

    fun getQuestionCount() = preferenceStorage.getInt("questionCount", 3)

    fun saveQuestionCount(count: Int) = preferenceStorage.putInt("questionCount", count)
}
