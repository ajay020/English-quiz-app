package com.example.englishquiz.di

import com.example.englishquiz.utils.managers.TimerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object QuizViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideTimerManager(): TimerManager = TimerManager(timerDuration = 25000L)
}
