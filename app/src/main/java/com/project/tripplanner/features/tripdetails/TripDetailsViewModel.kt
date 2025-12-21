package com.project.tripplanner.features.tripdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.ErrorState
import com.project.tripplanner.R
import com.project.tripplanner.data.KEY_LAST_OPENED_TRIP_ID
import com.project.tripplanner.data.KEY_LAST_OPENED_TRIP_DATE
import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.repositories.ItineraryRepository
import com.project.tripplanner.repositories.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryRepository,
    private val uiMapper: TripDetailsUiMapper,
    private val userPrefsStorage: UserPrefsStorage
) : BaseViewModel<TripDetailsEvent, TripDetailsUiState, TripDetailsEffect>(TripDetailsUiState()) {

    private val tripId: Long = savedStateHandle.get<Long>(Screen.TripDetails.ARG_TRIP_ID) ?: -1L
    private val selectedDate = MutableStateFlow<LocalDate?>(null)
    private var itineraryJob: Job? = null
    private var loadJob: Job? = null

    init {
        addEventHandler<TripDetailsEvent.ScreenLoaded>(::onScreenLoaded)
        addEventHandler<TripDetailsEvent.RetryClicked>(::onRetryClicked)
        addEventHandler<TripDetailsEvent.DaySelected>(::onDaySelected)
        addEventHandler<TripDetailsEvent.AddPlacesClicked>(::onAddPlacesClicked)
        addEventHandler<TripDetailsEvent.ReorderClicked>(::onReorderClicked)
        addEventHandler<TripDetailsEvent.DoneClicked>(::onDoneClicked)
        addEventHandler<TripDetailsEvent.ItineraryItemMoved>(::onItineraryItemMoved)
        addEventHandler<TripDetailsEvent.BackClicked>(::onBackClicked)
    }

    private fun onScreenLoaded(event: TripDetailsEvent.ScreenLoaded, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        loadTrip(emit, showLoading = true)
    }

    private fun onRetryClicked(event: TripDetailsEvent.RetryClicked, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        loadTrip(emit, showLoading = true)
    }

    private fun onDaySelected(event: TripDetailsEvent.DaySelected, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        selectedDate.value = event.date
        userPrefsStorage.put(KEY_LAST_OPENED_TRIP_DATE, event.date.toString())
        emit.updatedState<TripDetailsUiState> { current ->
            current.copy(
                selectedDate = event.date,
                days = uiMapper.updateSelectedDay(current.days, event.date)
            )
        }
    }

    private fun onAddPlacesClicked(
        event: TripDetailsEvent.AddPlacesClicked,
        emit: Emitter<TripDetailsUiState, TripDetailsEffect>
    ) {
        emit.effect(
            TripDetailsEffect.NavigateToActivityForm(
                tripId = tripId,
                date = state.value.selectedDate
            )
        )
    }

    private fun onReorderClicked(event: TripDetailsEvent.ReorderClicked, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        emit.updatedState<TripDetailsUiState> { current -> current.copy(isReorderMode = true) }
    }

    private fun onDoneClicked(event: TripDetailsEvent.DoneClicked, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        persistReorder(emit)
        emit.updatedState<TripDetailsUiState> { current -> current.copy(isReorderMode = false) }
    }

    private fun onItineraryItemMoved(
        event: TripDetailsEvent.ItineraryItemMoved,
        emit: Emitter<TripDetailsUiState, TripDetailsEffect>
    ) {
        emit.updatedState<TripDetailsUiState> { current ->
            if (event.fromIndex == event.toIndex) {
                current
            } else {
                val updated = current.itinerary.toMutableList()
                if (event.fromIndex in updated.indices && event.toIndex in updated.indices) {
                    val item = updated.removeAt(event.fromIndex)
                    val targetIndex = if (event.toIndex > event.fromIndex) event.toIndex - 1 else event.toIndex
                    updated.add(targetIndex, item)
                }
                current.copy(itinerary = updated)
            }
        }
    }

    private fun onBackClicked(event: TripDetailsEvent.BackClicked, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        emit.effect(TripDetailsEffect.NavigateBack)
    }

    private fun loadTrip(emit: Emitter<TripDetailsUiState, TripDetailsEffect>, showLoading: Boolean) {
        if (tripId <= 0L) {
            emit.updatedState<TripDetailsUiState> { current ->
                current.copy(isInitialLoading = false, error = ErrorState.UnknownError())
            }
            return
        }
        loadJob?.cancel()
        if (showLoading) {
            emit.updatedState<TripDetailsUiState> { current ->
                current.copy(isInitialLoading = true, error = null)
            }
        }
        loadJob = viewModelScope.launch {
            try {
                val trip = tripRepository.getTrip(tripId)
                if (trip == null) {
                    emit.updatedState<TripDetailsUiState> { current ->
                        current.copy(isInitialLoading = false, error = ErrorState.UnknownError())
                    }
                    return@launch
                }
                userPrefsStorage.put(KEY_LAST_OPENED_TRIP_ID, tripId)
                val initialDate = trip.startDate
                selectedDate.value = initialDate
                userPrefsStorage.put(KEY_LAST_OPENED_TRIP_DATE, initialDate.toString())
                emit.updatedState<TripDetailsUiState> { current ->
                    current.copy(
                        isInitialLoading = false,
                        error = null,
                        tripTitle = trip.destination,
                        tripDateRange = uiMapper.formatDateRange(trip.startDate, trip.endDate),
                        days = uiMapper.buildDayItems(trip.startDate, trip.endDate, initialDate),
                        selectedDate = initialDate
                    )
                }
                startObservingItinerary(emit)
            } catch (e: Exception) {
                emit.updatedState<TripDetailsUiState> { current ->
                    current.copy(isInitialLoading = false, error = ErrorState.UnknownError())
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObservingItinerary(emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        itineraryJob?.cancel()
        itineraryJob = viewModelScope.launch {
            selectedDate.filterNotNull()
                .flatMapLatest { date -> itineraryRepository.observeItineraryForDate(tripId, date) }
                .map(uiMapper::buildItineraryUiModels)
                .catch {
                    emit.effect(TripDetailsEffect.ShowSnackbar(R.string.trip_details_load_failed))
                }
                .collect { mapped ->
                    emit.updatedState<TripDetailsUiState> { current ->
                        if (current.isReorderMode) {
                            val currentIds = current.itinerary.map { it.id }
                            val mappedIds = mapped.map { it.id }
                            if (currentIds.size == mappedIds.size && currentIds.containsAll(mappedIds)) {
                                current
                            } else {
                                current.copy(itinerary = mapped)
                            }
                        } else {
                            current.copy(itinerary = mapped)
                        }
                    }
                }
        }
    }

    private fun persistReorder(emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        val orderedIds = state.value.itinerary.mapNotNull { it.id.toLongOrNull() }
        if (orderedIds.isEmpty()) return
        viewModelScope.launch {
            try {
                itineraryRepository.reorderItems(tripId, orderedIds)
            } catch (e: Exception) {
                emit.effect(TripDetailsEffect.ShowSnackbar(R.string.trip_details_reorder_failed))
            }
        }
    }
}
