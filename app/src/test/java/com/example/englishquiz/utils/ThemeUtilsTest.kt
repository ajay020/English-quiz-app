package com.example.englishquiz.utils

import com.example.englishquiz.R
import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 *  Unit tests for the ThemeUtils class.
 *
 */

class ThemeUtilsTest {
    @Test
    fun getSelectedThemeResourceId_classicTheme() {
        // Arrange
        val theme = "CLASSIC"
        val expectedResourceId = R.style.AppTheme_classic

        //
        val actualResourceId = ThemeUtils.getSelectedThemeResourceId(theme)

        //
        assertEquals(expectedResourceId, actualResourceId)
    }

    @Test
    fun getSelectedThemeResourceId_darkTheme() {
        val theme = "DARK"
        val expectedResourceId = R.style.AppTheme_dark
        val actualResourceId = ThemeUtils.getSelectedThemeResourceId(theme)
        assertEquals(expectedResourceId, actualResourceId)
    }

    @Test
    fun getSelectedThemeResourceId_natureTheme() {
        val theme = "NATURE"
        val expectedResourceId = R.style.AppTheme_nature
        val actualResourceId = ThemeUtils.getSelectedThemeResourceId(theme)
        assertEquals(expectedResourceId, actualResourceId)
    }

    @Test
    fun getSelectedThemeResourceId_oceanTheme() {
        val theme = "OCEAN"
        val expectedResourceId = R.style.AppTheme_ocean
        val actualResourceId = ThemeUtils.getSelectedThemeResourceId(theme)
        assertEquals(expectedResourceId, actualResourceId)
    }

    @Test
    fun getSelectedThemeResourceId_defaultTheme() {
        val theme = "INVALID_THEME"
        val expectedResourceId = R.style.AppTheme
        val actualResourceId = ThemeUtils.getSelectedThemeResourceId(theme)
        assertEquals(expectedResourceId, actualResourceId)
    }
}
