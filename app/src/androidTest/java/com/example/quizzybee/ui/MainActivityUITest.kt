package com.example.quizzybee.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quizzybee.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUITest {
    // start the main activity
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun start_quiz_button_navigates_to_quiz_activity() {
        // Click the Start Quiz button
        onView(withId(R.id.btn_start_quiz)).perform(click())

        // Verify that QuizActivity is started
        intended(hasComponent(QuizActivity::class.java.name))
    }

//    @Test
//    fun settings_button_shows_dialog() {
//        // Click the Settings button
//        onView(withId(R.id.btn_settings)).perform(click())
//
//        // Verify the settings dialog is displayed
//        onView(withId(R.id.dialog_settings)).check(matches(isDisplayed()))
//
//        // Verify the settings dialog contains settings text
//        onView(withText("Settings")).check(matches(isDisplayed()))
//    }
}
