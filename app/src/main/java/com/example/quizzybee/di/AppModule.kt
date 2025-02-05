package com.example.quizzybee.di

import android.content.Context
import com.example.quizzybee.data.database.AppDatabase
import com.example.quizzybee.data.database.QuestionDao
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.data.repository.QuestionRepository
import com.example.quizzybee.data.repository.QuestionRepositoryImpl
import com.example.quizzybee.notification.NotificationHelper
import com.example.quizzybee.notification.NotificationScheduler
import com.example.quizzybee.utils.managers.SoundManager
import com.example.quizzybee.utils.managers.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideQuestionRepository(questionDao: QuestionDao): QuestionRepository = QuestionRepositoryImpl(questionDao)

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = AppDatabase.getDatabase(context)

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

    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
    ): NotificationHelper = NotificationHelper(context)

    @Singleton
    @Provides
    fun provideNotificationScheduler(
        @ApplicationContext context: Context,
    ) = NotificationScheduler(context)

    @Singleton
    @Provides
    fun provideThemeManager(preferenceManager: PreferenceManager): ThemeManager = ThemeManager(preferenceManager)
}
