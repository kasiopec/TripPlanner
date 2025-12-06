package com.project.tripplanner.repositories

import com.project.tripplanner.data.local.db.TripDao
import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import com.project.tripplanner.data.local.entity.TripEntity
import com.project.tripplanner.data.local.entity.TripWithItineraryEntity
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.utils.TestClockProvider
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripRepositoryTest {

    private val tripDao: TripDao = mockk()
    private lateinit var repository: TripRepository
    private lateinit var clock: TestClockProvider

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        clock = TestClockProvider(ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("UTC")))
        repository = TripRepositoryImpl(tripDao = tripDao, clockProvider = clock)
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
        val capturedEntity = slot<TripEntity>()
        coEvery { tripDao.insertTrip(capture(capturedEntity)) } returns 1L

        val id = repository.createTrip(input)

        assertEquals(1L, id)
        val inserted = capturedEntity.captured
        assertEquals(clock.nowInstant(), inserted.createdAt)
        assertEquals(clock.nowInstant(), inserted.updatedAt)
        assertEquals(input.timezone.id, inserted.timezone)
    }

    @Test
    fun updateTrip_updatesOnlyUpdatedAt() = runTest {
        val createdAt = clock.nowInstant()
        val originalUpdatedAt = createdAt.minusSeconds(30)
        val trip = Trip(
            id = 12L,
            destination = "Paris",
            startDate = LocalDate.of(2025, 6, 1),
            endDate = LocalDate.of(2025, 6, 5),
            timezone = ZoneId.of("Europe/Paris"),
            coverImageUri = null,
            notes = "Initial notes",
            createdAt = createdAt,
            updatedAt = originalUpdatedAt
        )
        val updatedEntity = slot<TripEntity>()
        coEvery { tripDao.updateTrip(capture(updatedEntity)) } just Runs

        clock.advanceBy(Duration.ofHours(2))
        repository.updateTrip(trip)

        val persisted = updatedEntity.captured
        assertEquals(createdAt, persisted.createdAt)
        assertTrue(persisted.updatedAt.isAfter(originalUpdatedAt))
        assertEquals(clock.nowInstant(), persisted.updatedAt)
        assertEquals(trip.destination, persisted.destination)
        coVerify(exactly = 1) { tripDao.updateTrip(any()) }
    }

    @Test
    fun observeTripWithItinerary_returnsJoinedItems() = runTest {
        val tripEntity = TripEntity(
            id = 3L,
            destination = "Tokyo",
            startDate = LocalDate.of(2025, 7, 10),
            endDate = LocalDate.of(2025, 7, 15),
            timezone = ZoneId.of("Asia/Tokyo").id,
            coverImageUri = null,
            notes = null,
            createdAt = clock.nowInstant(),
            updatedAt = clock.nowInstant()
        )
        val itineraryEntity = ItineraryItemEntity(
            id = 7L,
            tripId = tripEntity.id,
            localDate = LocalDate.of(2025, 7, 10),
            localTime = LocalTime.of(9, 0),
            title = "Flight in",
            type = ItineraryType.Flight,
            location = "HND",
            notes = null,
            sortOrder = 1
        )
        coEvery { tripDao.observeTripWithItinerary(tripEntity.id) } returns flowOf(
            TripWithItineraryEntity(
                trip = tripEntity,
                itinerary = listOf(itineraryEntity)
            )
        )

        val tripWithItems = repository.observeTripWithItinerary(tripEntity.id).first()

        requireNotNull(tripWithItems)
        assertEquals("Tokyo", tripWithItems.trip.destination)
        assertEquals(1, tripWithItems.itinerary.size)
        assertEquals("Flight in", tripWithItems.itinerary.first().title)
    }
}
