package com.example.englishquiz.utils

import android.content.Context
import android.util.Log
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.preferences.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class QuestionJson(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val isSolved: Boolean = false,
)

object QuestionLoadingScript {
    fun importQuestionsFromJson(
        context: Context,
        database: AppDatabase,
    ) {
        val fileName = "questions.json"
        val preferenceManager = PreferenceManager(context)

        if (!preferenceManager.isDataLoaded()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val jsonContent = readJsonFromAssets(context, fileName)
                    val questions = parseJsonToQuestions(jsonContent)
                    val questionEntities = mapJsonToEntities(questions)
                    Log.d("DATA", "Questions: $questionEntities.size")
                    saveQuestionsToDatabase(database, questionEntities)
                    markDataAsLoaded(preferenceManager)

                    println("Questions imported successfully!")
                } catch (e: Exception) {
                    handleImportError(e)
                }
            }
        }
    }

    // 1️⃣ Read JSON File from Assets
    fun readJsonFromAssets(
        context: Context,
        fileName: String,
    ): String =
        context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

    // 2️⃣ Parse JSON into a List of Data Classes
    fun parseJsonToQuestions(json: String): List<QuestionJson> {
        val gson = Gson()
        val type = object : TypeToken<List<QuestionJson>>() {}.type
        return gson.fromJson(json, type)
    }

    // 3️⃣ Map JSON Data to Database Entities
    private fun mapJsonToEntities(questions: List<QuestionJson>): List<Question> =
        questions.map { question ->
            Question(
                questionText = question.questionText,
                options = question.options,
                correctAnswer = question.correctAnswer,
                isSolved = question.isSolved,
            )
        }

    // 4️⃣ Save Questions to the Database
    private suspend fun saveQuestionsToDatabase(
        database: AppDatabase,
        questions: List<Question>,
    ) {
        val questionDao = database.questionDao()
        questionDao.insertQuestionsInBulk(questions)
    }

    // 5️⃣ Mark Data as Loaded in Preferences
    private fun markDataAsLoaded(preferenceManager: PreferenceManager) {
        preferenceManager.setDataLoaded(true)
    }

    // 6️⃣ Handle Errors
    private fun handleImportError(e: Exception) {
        e.printStackTrace()
        println("Failed to import questions: ${e.message}")
    }
}
