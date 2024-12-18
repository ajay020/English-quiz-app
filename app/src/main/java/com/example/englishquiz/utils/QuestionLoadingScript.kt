package com.example.englishquiz.utils

import android.content.Context
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.database.Question
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
                    // Read the JSON file from assets
                    val jsonFile =
                        context.assets
                            .open(fileName)
                            .bufferedReader()
                            .use { it.readText() }

                    // Parse the JSON file
                    val gson = Gson()
                    val type = object : TypeToken<List<QuestionJson>>() {}.type
                    val questions: List<QuestionJson> = gson.fromJson(jsonFile, type)

                    // Map JSON data to database entities
                    val questionEntities =
                        questions.map { question ->
                            Question(
                                questionText = question.questionText,
                                options = question.options,
                                correctAnswer = question.correctAnswer,
                                isSolved = question.isSolved,
                            )
                        }

                    // Insert all questions in bulk
                    val questionDao = database.questionDao()
                    questionDao.insertQuestionsInBulk(questionEntities)

                    // Mark data as loaded
                    preferenceManager.setDataLoaded(true)

                    println("Questions imported successfully!")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Failed to import questions: ${e.message}")
                }
            }
        }
    }
}
