package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.data.model.TripWithItinerary
import com.project.tripplanner.features.tripform.domain.CreateTripUseCase
import com.project.tripplanner.features.tripform.domain.TripFormValidator
import com.project.tripplanner.features.tripform.domain.UpdateTripUseCase
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.TestClockProvider
import io.mockk.mockk
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var storage: FakeTripCoverImageStorage
    private lateinit var clockProvider: TestClockProvider
    private lateinit var viewModel: TripFormViewModel
    private lateinit var effectsJob: Job

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
    fun coverImageSelection_updatesPathAndPersistsOnCreate() = runTest(testDispatcher.scheduler) {
        repository = FakeTripRepository()
        storage = FakeTripCoverImageStorage()
        viewModel = createViewModel()
        collectEffects()

        viewModel.emitEvent(TripFormEvent.ScreenLoaded(null))
        advanceUntilIdle()

        storage.nextStoredPath = "cover_images/new-image.jpg"
        val pickerUri = mockk<Uri>()

        viewModel.emitEvent(TripFormEvent.CoverImageSelected(pickerUri))
        advanceUntilIdle()

        val formState = viewModel.state.value as TripFormUiState.Form
        assertEquals(null, formState.coverImagePath)
        assertEquals(pickerUri, formState.coverImageUri) // display still shows latest picker uri
        assertEquals(0, storage.importCallCount)

        val startDateMillis = LocalDate.of(2025, 2, 1).atStartOfDay(clockProvider.zoneId).toInstant().toEpochMilli()
        viewModel.emitEvent(TripFormEvent.DestinationChanged("Rome"))
        advanceUntilIdle()
        viewModel.emitEvent(TripFormEvent.StartDateSelected(startDateMillis))
        advanceUntilIdle()
        viewModel.emitEvent(TripFormEvent.EndDateSelected(startDateMillis))
        advanceUntilIdle()
        viewModel.emitEvent(TripFormEvent.SaveClicked)
        advanceUntilIdle()

        val createdTrip = repository.observeTrip(1).first()
        requireNotNull(createdTrip)
        assertEquals("cover_images/new-image.jpg", createdTrip.coverImageUri)
        assertEquals(1, storage.importCallCount)
    }

    @Test
    fun editFlow_keepsExistingCoverWhenUnchanged() = runTest(testDispatcher.scheduler) {
        val existingCoverPath = "cover_images/existing.jpg"
        val existingTrip = Trip(
            id = 10L,
            destination = "Kyoto",
            startDate = LocalDate.of(2025, 3, 10),
            endDate = LocalDate.of(2025, 3, 12),
            timezone = clockProvider.zoneId,
            coverImageUri = existingCoverPath,
            notes = "Spring plans",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        repository = FakeTripRepository(listOf(existingTrip))
        storage = FakeTripCoverImageStorage().apply {
            displayUriForPath[existingCoverPath] = mockk()
        }
        viewModel = createViewModel()
        collectEffects()

        viewModel.emitEvent(TripFormEvent.ScreenLoaded(existingTrip.id))
        advanceUntilIdle()

        val loadedState = viewModel.state.value as TripFormUiState.Form
        assertEquals(existingCoverPath, loadedState.coverImagePath)

        viewModel.emitEvent(TripFormEvent.NotesChanged("Updated notes"))
        viewModel.emitEvent(TripFormEvent.SaveClicked)
        advanceUntilIdle()

        val updatedTrip = repository.observeTrip(existingTrip.id).first()
        requireNotNull(updatedTrip)
        assertEquals(existingCoverPath, updatedTrip.coverImageUri)
        assertTrue(storage.deletedPaths.isEmpty())
    }

    @Test
    fun editFlow_deletesOldCoverWhenChanged() = runTest(testDispatcher.scheduler) {
        val existingCoverPath = "cover_images/existing.jpg"
        val newCoverPath = "cover_images/new.jpg"
        val existingTrip = Trip(
            id = 5L,
            destination = "Lisbon",
            startDate = LocalDate.of(2025, 4, 5),
            endDate = LocalDate.of(2025, 4, 8),
            timezone = clockProvider.zoneId,
            coverImageUri = existingCoverPath,
            notes = null,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        repository = FakeTripRepository(listOf(existingTrip))
        storage = FakeTripCoverImageStorage().apply {
            displayUriForPath[existingCoverPath] = mockk()
            nextStoredPath = newCoverPath
            displayUriForPath[newCoverPath] = mockk()
        }
        viewModel = createViewModel()
        collectEffects()

        viewModel.emitEvent(TripFormEvent.ScreenLoaded(existingTrip.id))
        advanceUntilIdle()

        val newPickerUri = mockk<Uri>()
        viewModel.emitEvent(TripFormEvent.CoverImageSelected(newPickerUri))
        advanceUntilIdle()
        viewModel.emitEvent(TripFormEvent.SaveClicked)
        advanceUntilIdle()

        val updatedTrip = repository.observeTrip(existingTrip.id).first()
        requireNotNull(updatedTrip)
        assertEquals(newCoverPath, updatedTrip.coverImageUri)
        assertTrue(storage.deletedPaths.contains(existingCoverPath))
        assertEquals(1, storage.importCallCount)
    }

    private fun TestScope.collectEffects() {
        effectsJob = backgroundScope.launch(testDispatcher) {
            viewModel.effect.collect { /* Drain effects to avoid blocking */ }
        }
    }

    private fun createViewModel(): TripFormViewModel {
        val validator = TripFormValidator()
        val createTripUseCase = CreateTripUseCase(repository)
        val updateTripUseCase = UpdateTripUseCase(repository, storage)
        return TripFormViewModel(
            tripRepository = repository,
            createTripUseCase = createTripUseCase,
            updateTripUseCase = updateTripUseCase,
            tripCoverImageStorage = storage,
            clockProvider = clockProvider,
            tripFormValidator = validator
        )
    }
}

private class FakeTripRepository(
    initialTrips: List<Trip> = emptyList()
) : TripRepository {

    private val tripsFlow = MutableStateFlow(initialTrips.associateBy { it.id })
    private var nextId = (initialTrips.maxOfOrNull { it.id } ?: 0L) + 1

    override fun observeTrips(): Flow<List<Trip>> {
        return tripsFlow.map { it.values.toList() }
    }

    override suspend fun getTrip(tripId: Long): Trip? = tripsFlow.value[tripId]

    override fun observeTrip(tripId: Long): Flow<Trip?> {
        return tripsFlow.map { it[tripId] }
    }

    override fun observeTripWithItinerary(tripId: Long): Flow<TripWithItinerary?> {
        return flowOf(tripsFlow.value[tripId]?.let { TripWithItinerary(it, emptyList()) })
    }

    override suspend fun createTrip(input: TripInput): Long {
        val id = nextId++
        val trip = Trip(
            id = id,
            destination = input.destination,
            startDate = input.startDate,
            endDate = input.endDate,
            timezone = input.timezone,
            coverImageUri = input.coverImageUri,
            notes = input.notes,
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )
        tripsFlow.update { current -> current + (id to trip) }
        return id
    }

    override suspend fun updateTrip(trip: Trip) {
        tripsFlow.update { current -> current + (trip.id to trip) }
    }

    override suspend fun deleteTrip(tripId: Long) {
        tripsFlow.update { current -> current - tripId }
    }

    override suspend fun deleteAllTrips() {
        tripsFlow.update { emptyMap() }
    }

    override suspend fun markAllTripsEnded() = Unit
}

private class FakeTripCoverImageStorage : TripCoverImageStorage {
    var nextStoredPath: String = "cover_images/generated.jpg"
    val displayUriForPath: MutableMap<String, Uri?> = mutableMapOf()
    val deletedPaths = mutableListOf<String?>()
    var importCallCount = 0
    var lastImportedUri: Uri? = null

    override suspend fun importFromPicker(sourceUri: Uri): String {
        importCallCount++
        lastImportedUri = sourceUri
        return nextStoredPath
    }

    override suspend fun resolveForDisplay(path: String?): Uri? {
        return path?.let { displayUriForPath[it] }
    }

    override suspend fun delete(path: String?) {
        deletedPaths.add(path)
    }
}
