package com.project.tripplanner.repositories

import android.content.Context
import androidx.room.Room
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import com.project.tripplanner.data.local.entity.TripEntity
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.utils.TestClockProvider
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
class TripRepositoryTest {

    private lateinit var database: TripPlannerDatabase
    private lateinit var repository: TripRepository
    private lateinit var clock: TestClockProvider

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TripPlannerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        clock = TestClockProvider(ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("UTC")))
        repository = TripRepositoryImpl(database.tripDao(), clock)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createTrip_savesWithTimestamps() = runTest {
        val input = TripInput(
            destination = "Lisbon",
            startDate = LocalDate.of(2025, 5, 10),
            endDate = LocalDate.of(2025, 5, 15),
            timezone = ZoneId.of("Europe/Lisbon"),
            coverImageUri = null,
            notes = "Spring trip"
        )

        val id = repository.createTrip(input)

        val trips = repository.observeTrips().first()
        val trip = trips.first()
        assertEquals(id, trip.id)
        assertEquals(clock.nowInstant(), trip.createdAt)
        assertEquals(clock.nowInstant(), trip.updatedAt)
        assertEquals("Europe/Lisbon", trip.timezone.id)
    }

    @Test
    fun updateTrip_updatesOnlyUpdatedAt() = runTest {
        val input = TripInput(
            destination = "Paris",
            startDate = LocalDate.of(2025, 6, 1),
            endDate = LocalDate.of(2025, 6, 5),
            timezone = ZoneId.of("Europe/Paris"),
            coverImageUri = null,
            notes = null
        )
        val id = repository.createTrip(input)
        val original = repository.observeTrip(id).firstOrNull()!!

        clock.advanceBy(Duration.ofHours(2))
        val updated = original.copy(destination = "Lyon", notes = "Replanned")
        repository.updateTrip(updated)

        val stored = repository.observeTrip(id).firstOrNull()!!
        assertEquals("Lyon", stored.destination)
        assertEquals("Replanned", stored.notes)
        assertEquals(original.createdAt, stored.createdAt)
        assertTrue(stored.updatedAt.isAfter(original.updatedAt))
    }

    @Test
    fun observeTripWithItinerary_returnsJoinedItems() = runTest {
        val tripEntity = TripEntity(
            destination = "Tokyo",
            startDate = LocalDate.of(2025, 7, 10),
            endDate = LocalDate.of(2025, 7, 15),
            timezone = ZoneId.of("Asia/Tokyo").id,
            coverImageUri = null,
            notes = null,
            createdAt = clock.nowInstant(),
            updatedAt = clock.nowInstant()
        )
        val tripId = database.tripDao().insertTrip(tripEntity)
        database.itineraryDao().insertItem(
            ItineraryItemEntity(
                tripId = tripId,
                localDate = LocalDate.of(2025, 7, 10),
                localTime = LocalTime.of(9, 0),
                title = "Flight in",
                type = ItineraryType.Flight,
                location = "HND",
                notes = null,
                sortOrder = 1
            )
        )

        val tripWithItems = repository.observeTripWithItinerary(tripId).first()
        requireNotNull(tripWithItems)
        assertEquals("Tokyo", tripWithItems.trip.destination)
        assertEquals(1, tripWithItems.itinerary.size)
        assertEquals("Flight in", tripWithItems.itinerary.first().title)
    }
}
