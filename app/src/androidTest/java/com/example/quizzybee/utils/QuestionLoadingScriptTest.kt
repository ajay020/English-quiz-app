package com.example.quizzybee.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

class QuestionLoadingScriptTest {
    @Test
    fun test_readJsonFromAsset_returns_correct_json() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fileName = "questions.json"

        val questionJson = QuestionLoadingScript.readJsonFromAssets(context, fileName)
        assert(questionJson.isNotEmpty())
    }

    @Test
    fun test_parseJsonToQuestions_returns_correct_list() {
        val json =
            """
            [
                {
                    "questionText": "What is the capital of France?",
                    "options": ["Paris", "London", "Berlin", "Madrid"],
                    "correctAnswer": "Paris",
                    "isSolved": false
                }
            ]
            """.trimIndent()

        val questions = QuestionLoadingScript.parseJsonToQuestions(json)
        assert(questions.size == 1)
        assert(questions[0].questionText == "What is the capital of France?")
        assert(questions[0].options.contains("London"))
        assert(questions[0].isSolved == false)
    }
}
