package com.project.tripplanner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import com.project.tripplanner.data.local.entity.TripEntity

@Database(
    entities = [TripEntity::class, ItineraryItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TripPlannerTypeConverters::class)
abstract class TripPlannerDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        const val NAME = "trip_planner.db"
    }
}
