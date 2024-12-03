package com.example.englishquiz

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.englishquiz.data.Question
import com.example.englishquiz.data.QuestionDao
import com.example.englishquiz.utils.Converters

// Quiz Database
@Database(entities = [Question::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class) // Register the Converters
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

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
