package com.example.englishquiz.views

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.R
import com.example.englishquiz.data.DayStatus
import com.example.englishquiz.data.preferences.PreferenceManager
import com.example.englishquiz.utils.managers.StreakManager

class StreakTrackerView(
    private val context: Context,
) {
    private val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    private val preferenceManager = PreferenceManager(context)
    private val streakManager = StreakManager(preferenceManager)
    private val daysStatuses = mutableListOf<DayStatus>()
    private val streakAdapter: StreakAdapter

    init {
        streakAdapter = StreakAdapter()
        refreshStreakData()
    }

    fun refreshStreakData() {
        daysStatuses.clear() // Clear the current statuses
        val streakData: Set<String> = preferenceManager.getStreakDates() // Retrieve saved dates
        val currentWeekStart = streakManager.getCurrentWeekStartDate() // Get the start date of the current week

        // Iterate through each day of the current week
        daysOfWeek.forEachIndexed { index, dayName ->
            val dayDate = streakManager.getDateForDayOfWeek(currentWeekStart, index) // Calculate the date for each day
            val isCompleted =
                streakData.any { savedDate ->
                    streakManager.isSameDay(savedDate, dayDate) // Check if this day is marked as completed
                }
            daysStatuses.add(DayStatus(dayName, isCompleted)) // Add the day status to the list
        }

        streakAdapter.notifyDataSetChanged() // Notify adapter of the changes
    }

    fun markDayAsCompleted() {
        streakManager.markDayAsCompleted()
    }

    private fun grantReward() {
        val currentCoins = preferenceManager.getCoins()
        preferenceManager.saveCoins(currentCoins + 50)
        preferenceManager.setRewardGranted(true)
    }

    fun isStreakCompleted(): Boolean = streakManager.isStreakCompleted()

    fun setupStreakTracker(recyclerView: RecyclerView) {
        recyclerView.adapter = streakAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false,
            )
    }

    // RecyclerView Adapter for Streak Display
    inner class StreakAdapter : RecyclerView.Adapter<StreakAdapter.StreakViewHolder>() {
        inner class StreakViewHolder(
            itemView: View,
        ) : RecyclerView.ViewHolder(itemView) {
            val icon: ImageView = itemView.findViewById(R.id.day_icon)
            val label: TextView = itemView.findViewById(R.id.day_label)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): StreakViewHolder {
            val view =
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_streak_day, parent, false)
            return StreakViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: StreakViewHolder,
            position: Int,
        ) {
            val dayStatus = daysStatuses[position]
            holder.label.text = dayStatus.dayName
            holder.icon.setImageResource(
                if (dayStatus.isCompleted) R.drawable.ic_check_circle else R.drawable.ic_check,
            )

            // Retrieve the color from the theme
            val colorPrimary =
                TypedValue()
                    .apply {
                        holder.itemView.context.theme
                            .resolveAttribute(androidx.appcompat.R.attr.colorPrimary, this, true)
                    }.data

            holder.icon.setColorFilter(
                if (dayStatus.isCompleted) {
                    colorPrimary // Use the primary color
                } else {
                    ContextCompat.getColor(holder.itemView.context, R.color.gray)
                },
            )
        }

        override fun getItemCount(): Int = daysStatuses.size
    }
}
