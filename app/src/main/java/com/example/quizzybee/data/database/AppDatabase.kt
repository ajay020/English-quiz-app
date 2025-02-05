package com.example.quizzybee.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quizzybee.data.Question
import com.example.quizzybee.utils.Converters

// Quiz Database
@Database(entities = [Question::class, SolvedDate::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class) // Register the Converters
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    abstract fun solvedDateDao(): SolvedDateDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance =
                        Room
                            .databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "quiz_database",
                            ).build()
                }
            }
            return instance!!
        }
    }
}
