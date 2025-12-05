package com.project.tripplanner.di.module

import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.data.local.db.ItineraryDao
import com.project.tripplanner.data.local.db.TripDao
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import com.project.tripplanner.repositories.ItineraryRepository
import com.project.tripplanner.repositories.ItineraryRepositoryImpl
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.repositories.TripRepositoryImpl
import com.project.tripplanner.repositories.UserPrefRepository
import com.project.tripplanner.repositories.UserPrefsRepositoryImp
import com.project.tripplanner.utils.time.ClockProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesUserPrefsRepository(userPrefsStorage: UserPrefsStorage): UserPrefRepository {
        return UserPrefsRepositoryImp(userPrefsStorage)
    }

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        clockProvider: ClockProvider
    ): TripRepository {
        return TripRepositoryImpl(tripDao, clockProvider)
    }

    @Provides
    @Singleton
    fun provideItineraryRepository(
        database: TripPlannerDatabase,
        itineraryDao: ItineraryDao
    ): ItineraryRepository {
        return ItineraryRepositoryImpl(database, itineraryDao)
    }
}
