package com.project.tripplanner.di.module

import com.project.tripplanner.utils.time.DateFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FormatterModule {
    @Provides
    @Singleton
    fun provideDateFormatter(): DateFormatter = DateFormatter
}
