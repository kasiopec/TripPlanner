package com.project.tripplanner.features.tripdetails

import androidx.lifecycle.SavedStateHandle
import com.project.tripplanner.R
import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.data.model.ItineraryItem
import com.project.tripplanner.data.model.ItineraryItemInput
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.repositories.ItineraryRepository
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.TestClockProvider
import com.project.tripplanner.utils.time.DateFormatter
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tripRepository = mockk<TripRepository>(relaxed = true)
    private val userPrefsStorage = mockk<UserPrefsStorage>(relaxed = true)
    private val uiMapper = TripDetailsUiMapper(DateFormatter)
    private lateinit var itineraryRepository: FakeItineraryRepository
    private lateinit var clockProvider: TestClockProvider
    private lateinit var viewModel: TripDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clockProvider = TestClockProvider(
            ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("UTC"))
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun mapClicked_withLocation_emitsOpenMap() = runTest(testDispatcher.scheduler) {
        createViewModel()

        val effectDeferred = async { viewModel.effect.first() }

        viewModel.emitEvent(
            TripDetailsEvent.MapClicked(
                itemId = "1",
                locationQuery = "Kyoto Station"
            )
        )
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is TripDetailsEffect.OpenMap)
        assertEquals("Kyoto Station", (effect as TripDetailsEffect.OpenMap).locationQuery)
    }

    @Test
    fun mapClicked_withoutLocation_opensLocationSheet() = runTest(testDispatcher.scheduler) {
        createViewModel()

        viewModel.emitEvent(
            TripDetailsEvent.MapClicked(
                itemId = "99",
                locationQuery = null
            )
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("99", state.locationSheetItemId)
        assertEquals("", state.locationQuery)
        assertEquals(false, state.isLocationActionEnabled)
    }

    @Test
    fun locationSaveClicked_updatesItemLocation() = runTest(testDispatcher.scheduler) {
        val item = ItineraryItem(
            id = 10L,
            tripId = 1L,
            localDate = LocalDate.of(2025, 1, 1),
            localTime = LocalTime.NOON,
            title = "Cafe",
            type = ItineraryType.Food,
            location = null,
            notes = null,
            sortOrder = 0
        )
        createViewModel(listOf(item))

        viewModel.emitEvent(
            TripDetailsEvent.MapClicked(
                itemId = item.id.toString(),
                locationQuery = null
            )
        )
        advanceUntilIdle()
        viewModel.emitEvent(TripDetailsEvent.LocationQueryChanged("Shibuya Crossing"))
        viewModel.emitEvent(TripDetailsEvent.LocationSaveClicked)
        advanceUntilIdle()

        val updated = itineraryRepository.updatedItems.single()
        assertEquals("Shibuya Crossing", updated.location)
    }

    @Test
    fun locationSaveClicked_missingItem_emitsSnackbar() = runTest(testDispatcher.scheduler) {
        createViewModel()

        val effectDeferred = async { viewModel.effect.first() }

        viewModel.emitEvent(
            TripDetailsEvent.MapClicked(
                itemId = "42",
                locationQuery = null
            )
        )
        advanceUntilIdle()
        viewModel.emitEvent(TripDetailsEvent.LocationQueryChanged("Madrid"))
        viewModel.emitEvent(TripDetailsEvent.LocationSaveClicked)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is TripDetailsEffect.ShowSnackbar)
        assertEquals(
            R.string.trip_details_location_save_failed,
            (effect as TripDetailsEffect.ShowSnackbar).messageResId
        )
    }

    private fun createViewModel(initialItems: List<ItineraryItem> = emptyList()) {
        itineraryRepository = FakeItineraryRepository(initialItems)
        viewModel = TripDetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(Screen.TripDetails.ARG_TRIP_ID to 1L)
            ),
            tripRepository = tripRepository,
            itineraryRepository = itineraryRepository,
            uiMapper = uiMapper,
            userPrefsStorage = userPrefsStorage,
            clockProvider = clockProvider
        )
    }
}

private class FakeItineraryRepository(
    initialItems: List<ItineraryItem>
) : ItineraryRepository {

    private val items = MutableStateFlow(initialItems.associateBy { it.id })
    val updatedItems = mutableListOf<ItineraryItem>()

    override fun observeItinerary(tripId: Long): Flow<List<ItineraryItem>> = flowOf(emptyList())

    override fun observeItineraryForDate(tripId: Long, localDate: LocalDate): Flow<List<ItineraryItem>> {
        return flowOf(emptyList())
    }

    override fun observeItem(itemId: Long): Flow<ItineraryItem?> {
        return items.map { it[itemId] }
    }

    override suspend fun addItem(input: ItineraryItemInput): Long = 0L

    override suspend fun updateItem(item: ItineraryItem) {
        updatedItems.add(item)
        items.update { current -> current + (item.id to item) }
    }

    override suspend fun deleteItem(itemId: Long) = Unit

    override suspend fun deleteItemsForTrip(tripId: Long) = Unit

    override suspend fun reorderItems(tripId: Long, localDate: LocalDate, orderedIds: List<Long>) = Unit
}
