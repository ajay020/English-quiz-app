package com.example.englishquiz

import android.content.Context

class PreferenceManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        const val SELECTED_THEME_KEY = "selected_theme"
        const val STREAK_DATA_KEY = "streak_data"
        const val REWARD_GRANTED_KEY = "reward_granted"
        const val CURRENT_LEVEL_KEY = "current_level"
        const val COINS_KEY = "coins"
        const val IS_DATA_LOADED_KEY = "is_data_loaded"
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
}
