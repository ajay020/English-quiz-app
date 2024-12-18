package com.example.englishquiz.utils

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `fromString should convert JSON string to list of strings`() {
        // Given a JSON string
        val jsonString = """["apple", "banana", "cherry"]"""

        // When fromString is called
        val result = converters.fromString(jsonString)

        // Then it should return the correct list
        val expected = listOf("apple", "banana", "cherry")
        assertEquals(expected, result)
    }

    @Test
    fun `fromString should handle empty JSON array`() {
        // Given an empty JSON array
        val jsonString = "[]"

        // When fromString is called
        val result = converters.fromString(jsonString)

        // Then it should return an empty list
        val expected = emptyList<String>()
        assertEquals(expected, result)
    }

    @Test
    fun `fromList should convert list of strings to JSON string`() {
        // Given a list of strings
        val list = listOf("apple", "banana", "cherry")

        // When fromList is called
        val result = converters.fromList(list)

        // Then it should return the correct JSON string
        val expected = """["apple","banana","cherry"]"""
        assertEquals(expected, result)
    }

    @Test
    fun `fromList should handle empty list`() {
        // Given an empty list
        val list = emptyList<String>()

        // When fromList is called
        val result = converters.fromList(list)

        // Then it should return an empty JSON array
        val expected = "[]"
        assertEquals(expected, result)
    }
}
