package com.project.tripplanner.repositories

import androidx.room.withTransaction
import com.project.tripplanner.data.local.db.ItineraryDao
import com.project.tripplanner.data.local.db.ItinerarySortUpdate
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import com.project.tripplanner.data.model.ItineraryItemInput
import com.project.tripplanner.data.model.ItineraryType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItineraryRepositoryTest {
    private val database: TripPlannerDatabase = mockk()
    private val itineraryDao: ItineraryDao = mockk()
    private lateinit var repository: ItineraryRepository

    private var nextId = 1L
    private val items = mutableListOf<ItineraryItemEntity>()
    private val itemsFlow = MutableStateFlow<List<ItineraryItemEntity>>(emptyList())

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction(any<suspend () -> Any>()) } coAnswers { call ->
            val block = call.invocation.args[1] as suspend () -> Any
            block.invoke()
        }
        setupDaoMocks()
        repository = ItineraryRepositoryImpl(database = database, itineraryDao = itineraryDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun addItem_assignsSortOrder() = runTest {
        val tripId = 1L
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
        val tripId = 2L
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
        val tripId = 3L
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

    private fun setupDaoMocks() {
        coEvery { itineraryDao.getMaxSortOrder(any()) } answers { call ->
            val tripId = call.invocation.args[0] as Long
            items.filter { it.tripId == tripId }.maxOfOrNull { it.sortOrder } ?: 0L
        }
        coEvery { itineraryDao.insertItem(any()) } answers { call ->
            val entity = call.invocation.args[0] as ItineraryItemEntity
            val stored = entity.copy(id = nextId++)
            items.add(stored)
            emitItems()
            stored.id
        }
        coEvery { itineraryDao.observeItinerary(any()) } answers { call ->
            val tripId = call.invocation.args[0] as Long
            itemsFlow.map { current ->
                current.filter { it.tripId == tripId }
                    .sortedWith(compareBy(ItineraryItemEntity::sortOrder, ItineraryItemEntity::id))
            }
        }
        coEvery { itineraryDao.observeItineraryForDate(any(), any()) } answers { call ->
            val tripId = call.invocation.args[0] as Long
            val localDate = call.invocation.args[1] as LocalDate
            itemsFlow.map { current ->
                current.filter { it.tripId == tripId && it.localDate == localDate }
                    .sortedWith(compareBy(ItineraryItemEntity::sortOrder, ItineraryItemEntity::id))
            }
        }
        coEvery { itineraryDao.getItemIdsForTrip(any()) } answers { call ->
            val tripId = call.invocation.args[0] as Long
            items.filter { it.tripId == tripId }
                .sortedWith(compareBy(ItineraryItemEntity::sortOrder, ItineraryItemEntity::id))
                .map { it.id }
        }
        coEvery { itineraryDao.updateSortOrders(any()) } answers { call ->
            val updates = call.invocation.args[0] as List<ItinerarySortUpdate>
            updates.forEach { update ->
                val index = items.indexOfFirst { it.id == update.id }
                if (index != -1) {
                    val entity = items[index]
                    items[index] = entity.copy(sortOrder = update.sortOrder)
                }
            }
            emitItems()
        }
        coEvery { itineraryDao.observeItem(any()) } answers { call ->
            val itemId = call.invocation.args[0] as Long
            itemsFlow.map { current -> current.firstOrNull { it.id == itemId } }
        }
        coEvery { itineraryDao.updateItem(any()) } answers { call ->
            val entity = call.invocation.args[0] as ItineraryItemEntity
            val index = items.indexOfFirst { it.id == entity.id }
            if (index != -1) {
                items[index] = entity
                emitItems()
            }
        }
        coEvery { itineraryDao.deleteItem(any()) } answers { call ->
            val itemId = call.invocation.args[0] as Long
            items.removeAll { it.id == itemId }
            emitItems()
        }
        coEvery { itineraryDao.deleteItemsForTrip(any()) } answers { call ->
            val tripId = call.invocation.args[0] as Long
            items.removeAll { it.tripId == tripId }
            emitItems()
        }
    }

    private fun emitItems() {
        itemsFlow.value = items.sortedWith(compareBy(ItineraryItemEntity::sortOrder, ItineraryItemEntity::id))
    }
}
