package com.example.quizzybee.utils

import android.content.Context
import android.util.Log
import com.example.quizzybee.data.Question
import com.example.quizzybee.data.database.AppDatabase
import com.example.quizzybee.data.preferences.PreferenceManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@JsonClass(generateAdapter = true)
data class QuestionJson(
    @Json(name = "questionText") val questionText: String,
    @Json(name = "options") val options: List<String>,
    @Json(name = "correctAnswer") val correctAnswer: String,
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
                    Log.d("DATA1", "JsonString: $jsonContent")

                    val questions = parseJsonToQuestions(jsonContent)
                    Log.d("DATA2", "Questions: $questions.size")

                    val questionEntities = mapJsonToEntities(questions)
                    Log.d("DATA3", "QuestionsEntities: $questionEntities")
                    saveQuestionsToDatabase(database, questionEntities)
                    markDataAsLoaded(preferenceManager)
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
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<List<QuestionJson>> =
            moshi.adapter(Types.newParameterizedType(List::class.java, QuestionJson::class.java))

        return adapter.fromJson(json) ?: emptyList()
    }

    // 3️⃣ Map JSON Data to Database Entities
    private fun mapJsonToEntities(questions: List<QuestionJson>): List<Question> =
        questions.map { question ->
            Question(
                questionText = question.questionText,
                options = question.options,
                correctAnswer = question.correctAnswer,
                isSolved = false, // Initialize isSolved as false
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
        Log.e("DATA4", "Failed to import questions: ${e.message}")
    }
}
