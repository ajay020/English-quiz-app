package com.example.quizzybee.di

import com.example.quizzybee.utils.managers.TimerManager
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
