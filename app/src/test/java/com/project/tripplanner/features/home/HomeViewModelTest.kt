package com.project.tripplanner.features.home

import android.net.Uri
import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.data.model.TripWithItinerary
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.TestClockProvider
import com.project.tripplanner.utils.time.CountdownFormatter
import com.project.tripplanner.utils.time.DateFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var clockProvider: TestClockProvider
    private lateinit var repository: FakeTripRepository
    private lateinit var storage: FakeTripCoverImageStorage
    private lateinit var viewModel: HomeViewModel
    private lateinit var effectsJob: Job
    private val capturedEffects = mutableListOf<HomeEffect>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clockProvider = TestClockProvider(
            ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("UTC"))
        )
    }

    @After
    fun tearDown() {
        if (::effectsJob.isInitialized) {
            effectsJob.cancel()
        }
        Dispatchers.resetMain()
    }

    @Test
    fun screenLoaded_selectsCurrentTripHeroWhenInProgress() = runTest(testDispatcher.scheduler) {
        clockProvider.setNow(ZonedDateTime.of(2025, 2, 2, 8, 0, 0, 0, ZoneId.of("UTC")))
        val inProgressTrip = Trip(
            id = 1L,
            destination = "Oslo",
            startDate = LocalDate.of(2025, 1, 30),
            endDate = LocalDate.of(2025, 2, 4),
            timezone = clockProvider.zoneId,
            coverImageUri = null,
            notes = null,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        val upcomingTrip = inProgressTrip.copy(
            id = 2L,
            destination = "Lisbon",
            startDate = LocalDate.of(2025, 3, 1),
            endDate = LocalDate.of(2025, 3, 10)
        )

        createViewModel(initialTrips = listOf(inProgressTrip, upcomingTrip))

        viewModel.emitEvent(HomeEvent.ScreenLoaded)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(inProgressTrip.id, state.currentTripId)
        assertNull(state.countdown)
        val heroTrip = state.trips.first { it.id == inProgressTrip.id }
        assertEquals(TripStatusUi.InProgress, heroTrip.status)
        assertNotNull(heroTrip.progress)
        assertEquals(4, heroTrip.progress?.currentDay)
        assertEquals(6, heroTrip.progress?.totalDays)
    }

    @Test
    fun screenLoaded_setsCountdownWhenNoCurrentTrip() = runTest(testDispatcher.scheduler) {
        clockProvider.setNow(ZonedDateTime.of(2025, 1, 1, 9, 0, 0, 0, ZoneId.of("UTC")))
        val earlyUpcoming = Trip(
            id = 5L,
            destination = "Kyoto",
            startDate = LocalDate.of(2025, 1, 5),
            endDate = LocalDate.of(2025, 1, 8),
            timezone = clockProvider.zoneId,
            coverImageUri = null,
            notes = null,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        val laterUpcoming = earlyUpcoming.copy(
            id = 6L,
            destination = "Tokyo",
            startDate = LocalDate.of(2025, 2, 1),
            endDate = LocalDate.of(2025, 2, 4)
        )

        createViewModel(initialTrips = listOf(earlyUpcoming, laterUpcoming))

        viewModel.emitEvent(HomeEvent.ScreenLoaded)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.currentTripId)
        assertEquals(earlyUpcoming.id, state.countdownTripId)
        assertNotNull(state.countdown)
        val countdownTrip = state.trips.first { it.id == earlyUpcoming.id }
        assertEquals(TripStatusUi.None, countdownTrip.status)
    }

    @Test
    fun refreshFailure_keepsTripsAndEmitsSnackbar() = runTest(testDispatcher.scheduler) {
        clockProvider.setNow(ZonedDateTime.of(2025, 4, 1, 10, 0, 0, 0, ZoneId.of("UTC")))
        val upcoming = Trip(
            id = 9L,
            destination = "Berlin",
            startDate = LocalDate.of(2025, 5, 10),
            endDate = LocalDate.of(2025, 5, 15),
            timezone = clockProvider.zoneId,
            coverImageUri = null,
            notes = null,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        createViewModel(initialTrips = listOf(upcoming))

        viewModel.emitEvent(HomeEvent.ScreenLoaded)
        advanceUntilIdle()

        repository.throwOnCallIndex = 2

        viewModel.emitEvent(HomeEvent.RefreshRequested)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.trips.isNotEmpty())
        assertTrue(capturedEffects.any { it is HomeEffect.ShowSnackbar })
    }

    @Test
    fun filterSelection_updatesActiveFilter() = runTest(testDispatcher.scheduler) {
        val endedTrip = Trip(
            id = 12L,
            destination = "Paris",
            startDate = LocalDate.of(2024, 5, 1),
            endDate = LocalDate.of(2024, 5, 5),
            timezone = clockProvider.zoneId,
            coverImageUri = null,
            notes = null,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        createViewModel(initialTrips = listOf(endedTrip))

        viewModel.emitEvent(HomeEvent.ScreenLoaded)
        advanceUntilIdle()

        viewModel.emitEvent(HomeEvent.FilterSelected(HomeFilterType.Ended))
        advanceUntilIdle()

        assertEquals(HomeFilterType.Ended, viewModel.state.value.activeFilter)
    }

    private fun createViewModel(initialTrips: List<Trip>): HomeViewModel {
        repository = FakeTripRepository(initialTrips)
        storage = FakeTripCoverImageStorage()
        val countdownFormatter = CountdownFormatter(clockProvider)
        capturedEffects.clear()
        viewModel = HomeViewModel(
            tripRepository = repository,
            clockProvider = clockProvider,
            countdownFormatter = countdownFormatter,
            tripCoverImageStorage = storage,
            dateFormatter = DateFormatter
        )
        effectsJob = backgroundScope.launch(testDispatcher) {
            viewModel.effect.collect { effect -> capturedEffects.add(effect) }
        }
        return viewModel
    }
}

private class FakeTripRepository(
    initialTrips: List<Trip>
) : TripRepository {

    private val tripsFlow = MutableStateFlow(initialTrips.associateBy { it.id })
    var observeCallCount = 0
    var throwOnCallIndex: Int? = null

    override fun observeTrips(): Flow<List<Trip>> {
        observeCallCount++
        val callIndex = observeCallCount
        return flow {
            if (throwOnCallIndex == callIndex) throw IllegalStateException("observeTrips failure")
            emitAll(tripsFlow.map { it.values.toList() })
        }
    }

    override fun observeTrip(tripId: Long): Flow<Trip?> {
        return tripsFlow.map { it[tripId] }
    }

    override fun observeTripWithItinerary(tripId: Long): Flow<TripWithItinerary?> {
        return tripsFlow.map { trips -> trips[tripId]?.let { TripWithItinerary(it, emptyList()) } }
    }

    override suspend fun createTrip(input: TripInput): Long {
        val nextId = (tripsFlow.value.keys.maxOrNull() ?: 0L) + 1
        val trip = Trip(
            id = nextId,
            destination = input.destination,
            startDate = input.startDate,
            endDate = input.endDate,
            timezone = input.timezone,
            coverImageUri = input.coverImageUri,
            notes = input.notes,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        tripsFlow.update { current -> current + (trip.id to trip) }
        return nextId
    }

    override suspend fun updateTrip(trip: Trip) {
        tripsFlow.update { current -> current + (trip.id to trip) }
    }

    override suspend fun deleteTrip(tripId: Long) {
        tripsFlow.update { current -> current - tripId }
    }
}

private class FakeTripCoverImageStorage : TripCoverImageStorage {
    var resolved: Uri? = null
    override suspend fun importFromPicker(sourceUri: Uri): String = "imported"

    override suspend fun resolveForDisplay(path: String?): Uri? = resolved

    override suspend fun delete(path: String?) = Unit
}
