package com.example.quizzybee.di

import android.content.Context
import androidx.room.Room
import com.example.quizzybee.data.database.AppDatabase
import com.example.quizzybee.data.database.QuestionDao
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.data.repository.QuestionRepository
import com.example.quizzybee.data.repository.QuestionRepositoryImpl
import com.example.quizzybee.utils.managers.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class],
)
@Module
class TestAppModule {
    @Singleton
    @Provides
    fun provideTestDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java,
            ).allowMainThreadQueries()
            .build()

    @Provides
    fun provideQuestionRepository(questionDao: QuestionDao): QuestionRepository = QuestionRepositoryImpl(questionDao)

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Singleton
    @Provides
    fun provideSoundManager(
        @ApplicationContext context: Context,
        preferenceManager: PreferenceManager,
    ): SoundManager = SoundManager(preferenceManager, context)

    @Singleton
    @Provides
    fun providePreferenceManager(
        @ApplicationContext context: Context,
    ): PreferenceManager = PreferenceManager(context)
}
