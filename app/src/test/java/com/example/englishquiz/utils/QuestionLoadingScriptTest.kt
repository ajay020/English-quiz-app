package com.example.englishquiz.utils

import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionLoadingScriptTest {
    @Test
    fun test_parseJsonToStrings_returns_correct_list() {
        // Arrange
        val jsonString =
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

        val expectedList =
            listOf(
                QuestionJson(
                    questionText = "What is the capital of France?",
                    options = listOf("Paris", "London", "Berlin", "Madrid"),
                    correctAnswer = "Paris",
                    isSolved = false,
                ),
            )

        // Act
        val actualList = QuestionLoadingScript.parseJsonToQuestions(jsonString)

        // Assert
        assertEquals(expectedList, actualList)
    }

    @Test
    fun test_parseJsonToStrings_returns_empty_list_for_empty_json() {
        // Arrange
        val jsonString = "[]"
        val expectedList = emptyList<QuestionJson>()

        // Act
        val actualList = QuestionLoadingScript.parseJsonToQuestions(jsonString)

        // Assert
        assertEquals(expectedList, actualList)
    }

    @Test(expected = JsonSyntaxException::class)
    fun test_parseJsonToStrings_throw_json_syntax_exception_for_invalid_json() {
        // Arrange
        val invalidJsonString =
            """
            [
                 {
                    "questionText": "What is the capital of France?",
                    "options": ["Paris", "London", "Berlin", "Madrid"],
                    "correctAnswer": "Paris"
                    "isSolved": false
                }
            ]
            """.trimIndent()

        // Act
        QuestionLoadingScript.parseJsonToQuestions(invalidJsonString)
    }
}
