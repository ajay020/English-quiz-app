package com.example.englishquiz.data.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "solved_dates")
data class SolvedDate(
    // Format: "yyyy-MM-dd"
    @PrimaryKey val date: String,
)

@Dao
interface SolvedDateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSolvedDate(date: SolvedDate)

    @Query("SELECT * FROM solved_dates")
    suspend fun getAllSolvedDates(): List<SolvedDate>
}
