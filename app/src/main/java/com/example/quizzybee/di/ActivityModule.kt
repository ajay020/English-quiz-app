package com.example.quizzybee.di

import android.content.Context
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.utils.managers.DialogManager
import com.example.quizzybee.utils.managers.SoundManager
import com.example.quizzybee.utils.managers.StreakManager
import com.example.quizzybee.views.StreakTrackerView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @ActivityScoped
    @Provides
    fun provideDialogManager(
        @ActivityContext context: Context,
        soundManager: SoundManager,
        preferenceManager: PreferenceManager,
    ): DialogManager = DialogManager(context, soundManager, preferenceManager)

    @Provides
    @ActivityScoped
    fun provideStreakTrackerView(
        @ActivityContext context: Context,
        preferenceManager: PreferenceManager,
        streakManager: StreakManager,
    ): StreakTrackerView = StreakTrackerView(context, preferenceManager, streakManager)

    @Provides
    @ActivityScoped
    fun provideStreakManager(preferenceManager: PreferenceManager): StreakManager = StreakManager(preferenceManager)
}
