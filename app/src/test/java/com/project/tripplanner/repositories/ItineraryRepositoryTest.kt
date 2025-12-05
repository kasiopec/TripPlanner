package com.project.tripplanner.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import com.project.tripplanner.data.mapper.toEntity
import com.project.tripplanner.data.model.ItineraryItemInput
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.utils.TestClockProvider
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItineraryRepositoryTest {
    private lateinit var database: TripPlannerDatabase
    private lateinit var repository: ItineraryRepository
    private lateinit var clock: TestClockProvider

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TripPlannerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        clock = TestClockProvider(ZonedDateTime.of(2025, 2, 1, 9, 0, 0, 0, ZoneId.of("UTC")))
        repository = ItineraryRepositoryImpl(database, database.itineraryDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addItem_assignsSortOrder() = runTest {
        val tripId = createTrip()
        val date = LocalDate.of(2025, 2, 10)
        val firstInput = ItineraryItemInput(
            tripId = tripId,
            localDate = date,
            localTime = LocalTime.of(8, 0),
            title = "Flight out",
            type = ItineraryType.Flight,
            location = "LAX",
            notes = null,
            sortOrder = null
        )
        val firstId = repository.addItem(firstInput)
        val secondInput = ItineraryItemInput(
            tripId = tripId,
            localDate = date,
            localTime = null,
            title = "Check-in",
            type = ItineraryType.Hotel,
            location = "Downtown",
            notes = null,
            sortOrder = null
        )
        val secondId = repository.addItem(secondInput)

        val items = repository.observeItinerary(tripId).first()
        assertEquals(2, items.size)
        val firstItem = items.first { it.id == firstId }
        val secondItem = items.first { it.id == secondId }
        assertEquals(LocalTime.of(8, 0).toSecondOfDay().toLong(), firstItem.sortOrder)
        assertEquals(firstItem.sortOrder + 1, secondItem.sortOrder)
    }

    @Test
    fun reorderItems_updatesOrder() = runTest {
        val tripId = createTrip()
        val date = LocalDate.of(2025, 3, 1)
        val firstId = repository.addItem(
            ItineraryItemInput(
                tripId = tripId,
                localDate = date,
                localTime = LocalTime.of(9, 0),
                title = "Breakfast",
                type = ItineraryType.Food,
                location = "Cafe",
                notes = null,
                sortOrder = null
            )
        )
        val secondId = repository.addItem(
            ItineraryItemInput(
                tripId = tripId,
                localDate = date,
                localTime = LocalTime.of(11, 0),
                title = "Museum",
                type = ItineraryType.Activity,
                location = "City Museum",
                notes = null,
                sortOrder = null
            )
        )

        repository.reorderItems(tripId, listOf(secondId, firstId))

        val items = repository.observeItinerary(tripId).first()
        assertEquals(listOf(secondId, firstId), items.sortedBy { it.sortOrder }.map { it.id })
        val sortOrders = items.associate { it.id to it.sortOrder }
        assertEquals(0L, sortOrders.getValue(secondId))
        assertEquals(1L, sortOrders.getValue(firstId))
    }

    @Test
    fun observeItineraryForDate_filtersItems() = runTest {
        val tripId = createTrip()
        val firstDate = LocalDate.of(2025, 4, 1)
        val secondDate = LocalDate.of(2025, 4, 2)
        repository.addItem(
            ItineraryItemInput(
                tripId = tripId,
                localDate = firstDate,
                localTime = null,
                title = "Arrive",
                type = ItineraryType.Flight,
                location = null,
                notes = null,
                sortOrder = null
            )
        )
        repository.addItem(
            ItineraryItemInput(
                tripId = tripId,
                localDate = secondDate,
                localTime = null,
                title = "Explore",
                type = ItineraryType.Activity,
                location = null,
                notes = null,
                sortOrder = null
            )
        )

        val dayOneItems = repository.observeItineraryForDate(tripId, firstDate).first()
        assertEquals(1, dayOneItems.size)
        assertEquals("Arrive", dayOneItems.first().title)
    }

    private suspend fun createTrip(): Long {
        val tripInput = TripInput(
            destination = "Test",
            startDate = LocalDate.of(2025, 2, 10),
            endDate = LocalDate.of(2025, 2, 12),
            timezone = ZoneId.of("UTC"),
            coverImageUri = null,
            notes = null
        )
        return database.tripDao().insertTrip(tripInput.toEntity(clock.nowInstant()))
    }
}
