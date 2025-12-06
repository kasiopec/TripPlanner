package com.project.tripplanner.di.module

import android.content.Context
import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.cover.TripCoverImageStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoverImageModule {

    @Provides
    @Singleton
    fun provideTripCoverImageStorage(
        @ApplicationContext context: Context
    ): TripCoverImageStorage {
        return TripCoverImageStorageImpl(context)
    }
}
