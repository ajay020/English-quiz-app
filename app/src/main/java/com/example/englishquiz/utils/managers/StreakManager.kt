package com.example.englishquiz.utils.managers

import com.example.englishquiz.data.preferences.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class StreakManager
    @Inject
    constructor(
        private val preferenceManager: PreferenceManager,
    ) {
        companion object {
            const val TOTAL_DAYS_IN_WEEK = 7
        }

        fun addCompletedDat(date: String) {
            val streakDates = preferenceManager.getStreakDates().toMutableSet()

            if (!streakDates.contains(date)) {
                streakDates.add(date)
                preferenceManager.saveStreakDates(streakDates)
            }
        }

        fun getCurrentWeekStartDate(): Calendar {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            return calendar
        }

        fun getDateForDayOfWeek(
            weekStart: Calendar,
            dayIndex: Int,
        ): String {
            val calendar = weekStart.clone() as Calendar
            calendar.add(Calendar.DAY_OF_WEEK, dayIndex)
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        }

        /**
         *
         */
        fun isSameDay(
            date1: String,
            date2: String,
        ): Boolean {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.parse(date1) == sdf.parse(date2)
        }

        /**
         * Marks the current day as completed in the streak.
         */
        fun markDayAsCompleted() {
            val currentDate = getCurrentDate()
            val streakDates = preferenceManager.getStreakDates().toMutableSet()

            if (!streakDates.contains(currentDate)) {
                streakDates.add(currentDate)
                preferenceManager.saveStreakDates(streakDates)
            }
        }

        /**
         *
         */
        fun resetIfWeekChanged() {
            val streakDates = preferenceManager.getStreakDates()

            if (streakDates.isNotEmpty()) {
                val lastDate = streakDates.maxOrNull() ?: return
                if (!isSameWeek(lastDate, getCurrentDate())) {
                    resetStreak()
                }
            }
        }

        /**
         * Checks if the streak is completed for the week.
         */
        fun isStreakCompleted(): Boolean {
            resetIfWeekChanged()

            val currentWeekDates =
                preferenceManager.getStreakDates().filter { isSameWeek(it, getCurrentDate()) }
            return currentWeekDates.size == TOTAL_DAYS_IN_WEEK
        }

        /**
         * Grants the reward if the streak is completed and the reward has not been granted.
         */
        fun grantRewardIfEligible(): Boolean {
            if (isStreakCompleted() && !preferenceManager.isRewardGranted()) {
                preferenceManager.setRewardGranted(true)
                return true // Reward granted
            }
            return false // No reward granted
        }

        /**
         * Clears the streak data (useful for resetting at the end of the week).
         */
        fun resetStreak() {
            preferenceManager.clearStreakDates()
            preferenceManager.setRewardGranted(false)
        }

        /**
         * Gets the current date
         */
        private fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date())
        }

        /**
         * Checks if two dates are in the same week.
         */
        private fun isSameWeek(
            date1: String,
            date2: String,
        ): Boolean {
            val calendar1 =
                Calendar
                    .getInstance()
                    .apply { time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date1)!! }
            val calendar2 =
                Calendar
                    .getInstance()
                    .apply { time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date2)!! }

            return calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        }
    }
