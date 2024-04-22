package com.project.tripplanner.di.module

import android.content.Context
import com.project.tripplanner.data.UserPrefsStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefsModule {

    @Provides
    @Singleton
    fun providesUserSharedPrefs(@ApplicationContext context: Context): UserPrefsStorage {
        return UserPrefsStorage(context)
    }
}