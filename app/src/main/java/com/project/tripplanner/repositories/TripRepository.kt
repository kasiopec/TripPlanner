package com.project.tripplanner.repositories

import com.project.tripplanner.data.local.db.TripDao
import com.project.tripplanner.data.mapper.toDomain
import com.project.tripplanner.data.mapper.toEntity
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.data.model.TripWithItinerary
import com.project.tripplanner.utils.time.ClockProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeTrip(tripId: Long): Flow<Trip?>
    fun observeTripWithItinerary(tripId: Long): Flow<TripWithItinerary?>
    suspend fun createTrip(input: TripInput): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(tripId: Long)
    suspend fun deleteAllTrips()
    suspend fun markAllTripsEnded()
}

class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val clockProvider: ClockProvider
) : TripRepository {
    override fun observeTrips(): Flow<List<Trip>> {
        return tripDao.observeTrips().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeTrip(tripId: Long): Flow<Trip?> {
        return tripDao.observeTrip(tripId).map { it?.toDomain() }
    }

    override fun observeTripWithItinerary(tripId: Long): Flow<TripWithItinerary?> {
        return tripDao.observeTripWithItinerary(tripId).map { it?.toDomain() }
    }

    override suspend fun createTrip(input: TripInput): Long {
        val timestamp = clockProvider.nowInstant()
        val entity = input.toEntity(timestamp)
        return tripDao.insertTrip(entity)
    }

    override suspend fun updateTrip(trip: Trip) {
        val updatedTrip = trip.copy(updatedAt = clockProvider.nowInstant())
        tripDao.updateTrip(updatedTrip.toEntity())
    }

    override suspend fun deleteTrip(tripId: Long) {
        tripDao.deleteTrip(tripId)
    }

    override suspend fun deleteAllTrips() {
        tripDao.deleteAllTrips()
    }

    override suspend fun markAllTripsEnded() {
        val targetDate = clockProvider.now().toLocalDate().minusDays(1)
        val updatedAt = clockProvider.nowInstant()
        tripDao.markAllTripsEnded(targetDate = targetDate, updatedAt = updatedAt)
    }
}
