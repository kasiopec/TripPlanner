package com.project.tripplanner.di.module

import android.content.Context
import androidx.room.Room
import com.project.tripplanner.data.local.db.ItineraryDao
import com.project.tripplanner.data.local.db.TripDao
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TripPlannerDatabase {
        return Room.databaseBuilder(
            context,
            TripPlannerDatabase::class.java,
            TripPlannerDatabase.NAME
        ).build()
    }

    @Provides
    fun provideTripDao(database: TripPlannerDatabase): TripDao = database.tripDao()

    @Provides
    fun provideItineraryDao(database: TripPlannerDatabase): ItineraryDao = database.itineraryDao()
}
