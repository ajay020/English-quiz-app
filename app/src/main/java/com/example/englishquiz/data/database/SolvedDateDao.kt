package com.example.englishquiz.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolvedDateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSolvedDate(date: SolvedDate)

    @Query("SELECT * FROM solved_dates")
    fun getAllSolvedDates(): Flow<List<SolvedDate>>
}
