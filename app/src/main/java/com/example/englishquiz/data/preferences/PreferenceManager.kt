package com.example.englishquiz.data.preferences

import android.content.Context
import com.example.englishquiz.utils.managers.Theme
import javax.inject.Inject

class PreferenceManager
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        companion object {
            const val SELECTED_THEME_KEY = "selected_theme"
            const val STREAK_DATA_KEY = "streak_data"
            const val REWARD_GRANTED_KEY = "reward_granted"
            const val CURRENT_LEVEL_KEY = "current_level"
            const val COINS_KEY = "coins"
            const val IS_DATA_LOADED_KEY = "is_data_loaded"
            const val IS_SOUND_ENABLED_KEY = "is_sound_enabled"
            const val IS_MUSIC_ENABLED_KEY = "is_music_enabled"
            const val TOTAL_TIME_SPENT = "total_time_spent"
            private const val KEY_FIRST_LAUNCH_DATE = "first_launch_date"
            private const val KEY_QUIZ_COMPLETION_DATE = "quiz_completion_date"
        }

        // theme functions
        fun getSelectedThemeFromPreferences(): Theme {
            val themeName = sharedPreferences.getString(SELECTED_THEME_KEY, Theme.CLASSIC.name)
            return Theme.valueOf(themeName ?: Theme.CLASSIC.name)
        }

        fun saveSelectedThemeToPreferences(theme: Theme) {
            sharedPreferences.edit().putString(SELECTED_THEME_KEY, theme.name).apply()
        }

        fun saveStreakDates(dates: Set<String>) {
            sharedPreferences.edit().putStringSet("streak_dates", dates).apply()
        }

        fun getStreakDates(): Set<String> = sharedPreferences.getStringSet("streak_dates", emptySet()) ?: emptySet()

        fun clearStreakDates() {
            sharedPreferences.edit().remove("streak_dates").apply()
        }

        // **Streak Data Functions**
        fun getStreakData(): Set<Int> {
            val streakData = sharedPreferences.getStringSet(STREAK_DATA_KEY, emptySet())
            return streakData?.map { it.toInt() }?.toSet() ?: emptySet()
        }

        // **Reward Granted Function**
        fun isRewardGranted(): Boolean = sharedPreferences.getBoolean(REWARD_GRANTED_KEY, false)

        fun setRewardGranted(granted: Boolean) {
            sharedPreferences.edit().putBoolean(REWARD_GRANTED_KEY, granted).apply()
        }

        // **Level Data Functions**
        fun saveCurrentLevel(level: Int) {
            sharedPreferences.edit().putInt(CURRENT_LEVEL_KEY, level).apply()
        }

        fun getCurrentLevel(): Int = sharedPreferences.getInt(CURRENT_LEVEL_KEY, 1)

        // **Coins Functions**
        fun saveCoins(amount: Int) {
            sharedPreferences.edit().putInt(COINS_KEY, amount).apply()
        }

        fun getCoins() = sharedPreferences.getInt(COINS_KEY, 200)

        // **Data Loaded Function**
        fun isDataLoaded(): Boolean = sharedPreferences.getBoolean(IS_DATA_LOADED_KEY, false)

        fun setDataLoaded(loaded: Boolean) {
            sharedPreferences.edit().putBoolean(IS_DATA_LOADED_KEY, loaded).apply()
        }

        // **Sound Functions**
        fun isSoundEnabled(): Boolean = sharedPreferences.getBoolean(IS_SOUND_ENABLED_KEY, true)

        fun setSoundEnabled(enabled: Boolean) {
            sharedPreferences.edit().putBoolean(IS_SOUND_ENABLED_KEY, enabled).apply()
        }

        // **Music Functions**
        fun isMusicEnabled(): Boolean = sharedPreferences.getBoolean(IS_MUSIC_ENABLED_KEY, true)

        fun setMusicEnabled(enabled: Boolean) {
            sharedPreferences.edit().putBoolean(IS_MUSIC_ENABLED_KEY, enabled).apply()
        }

        //
        fun saveTotalTimeSpent(timeInMillis: Long) {
            sharedPreferences.edit().putLong(TOTAL_TIME_SPENT, timeInMillis).apply()
        }

        fun getTotalTimeSpent(): Long = sharedPreferences.getLong(TOTAL_TIME_SPENT, 0L)

        // Save the first launch date
        fun saveFirstLaunchDate() {
            if (!sharedPreferences.contains(KEY_FIRST_LAUNCH_DATE)) {
                val currentDate = System.currentTimeMillis()
                sharedPreferences.edit().putLong(KEY_FIRST_LAUNCH_DATE, currentDate).apply()
            }
        }

        // Get the first launch date
        fun getFirstLaunchDate(): Long = sharedPreferences.getLong(KEY_FIRST_LAUNCH_DATE, System.currentTimeMillis())

        // Save the quiz completion date
        fun saveQuizCompletionDate() {
            val currentDate = System.currentTimeMillis()
            sharedPreferences.edit().putLong(KEY_QUIZ_COMPLETION_DATE, currentDate).apply()
        }

        // Get the quiz completion date
        fun getQuizCompletionDate(): Long = sharedPreferences.getLong(KEY_QUIZ_COMPLETION_DATE, System.currentTimeMillis())

        // Calculate days between first launch and quiz completion
        fun getDaysBetweenLaunchAndCompletion(): Int {
            val firstLaunch = getFirstLaunchDate()
            val completion = getQuizCompletionDate()
            val differenceInMillis = completion - firstLaunch
            return (differenceInMillis / (1000 * 60 * 60 * 24)).toInt()
        }
    }
