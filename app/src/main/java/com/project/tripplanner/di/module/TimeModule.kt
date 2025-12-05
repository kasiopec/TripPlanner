package com.project.tripplanner.di.module

import com.project.tripplanner.utils.time.ClockProvider
import com.project.tripplanner.utils.time.DefaultClockProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    @Singleton
    abstract fun bindClockProvider(impl: DefaultClockProvider): ClockProvider

    companion object {
        @Provides
        @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()
    }
}
