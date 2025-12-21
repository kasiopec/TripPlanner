package com.project.tripplanner.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.project.tripplanner.data.local.entity.TripEntity
import com.project.tripplanner.data.local.entity.TripWithItineraryEntity
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate, id")
    fun observeTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun observeTrip(tripId: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTrip(tripId: Long): TripEntity?

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun observeTripWithItinerary(tripId: Long): Flow<TripWithItineraryEntity?>

    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: Long)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query(
        """
        UPDATE trips
        SET 
            startDate = CASE WHEN startDate > :targetDate THEN :targetDate ELSE startDate END,
            endDate = :targetDate,
            updatedAt = :updatedAt
        """
    )
    suspend fun markAllTripsEnded(targetDate: LocalDate, updatedAt: Instant)
}
