package com.example.englishquiz.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solved_dates")
data class SolvedDate(
    // Format: "yyyy-MM-dd"
    @PrimaryKey val date: String,
)
