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
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.navigation.Screen
import com.project.tripplanner.repositories.ItineraryRepository
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.time.ClockProvider
import com.project.tripplanner.utils.time.TripDateStatus
import com.project.tripplanner.utils.time.getTripDateStatus
import com.project.tripplanner.utils.time.nowLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch

private const val ITINERARY_RETRY_DELAY_MS = 500L

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryRepository,
    private val uiMapper: TripDetailsUiMapper,
    private val userPrefsStorage: UserPrefsStorage,
    private val clockProvider: ClockProvider
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
                days = uiMapper.updateSelectedDay(current.days, event.date),
                isReorderMode = false
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
        emit.updatedState<TripDetailsUiState> { current ->
            val reordered = event.orderedIds.mapNotNull { id -> current.itinerary.find { it.id == id } }
            current.copy(
                itinerary = reordered.ifEmpty { current.itinerary },
                isReorderMode = false
            )
        }
        persistReorder(event.orderedIds, emit)
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
                val nowDate = clockProvider.nowLocalDate(trip.timezone)
                val initialDate = resolveInitialSelectedDate(trip, nowDate)
                selectedDate.value = initialDate
                userPrefsStorage.put(KEY_LAST_OPENED_TRIP_DATE, initialDate.toString())
                emit.updatedState<TripDetailsUiState> { current ->
                    current.copy(
                        isInitialLoading = false,
                        error = null,
                        tripTitle = trip.destination,
                        tripDateRange = uiMapper.formatDateRange(trip.startDate, trip.endDate),
                        tripStatusLabelResId = uiMapper.getTripStatusLabelResId(
                            startDate = trip.startDate,
                            endDate = trip.endDate,
                            nowDate = nowDate
                        ),
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
                .flatMapLatest { date ->
                    itineraryRepository.observeItineraryForDate(tripId, date)
                        .map(uiMapper::buildItineraryUiModels)
                        .retryWhen { _, _ ->
                            emit.effect(TripDetailsEffect.ShowSnackbar(R.string.trip_details_load_failed))
                            delay(ITINERARY_RETRY_DELAY_MS)
                            true
                        }
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

    private fun persistReorder(orderedIds: List<String>, emit: Emitter<TripDetailsUiState, TripDetailsEffect>) {
        val orderedLongIds = orderedIds.mapNotNull { it.toLongOrNull() }
        if (orderedLongIds.isEmpty()) return
        val currentDate = selectedDate.value ?: return
        viewModelScope.launch {
            try {
                itineraryRepository.reorderItems(tripId, currentDate, orderedLongIds)
            } catch (e: Exception) {
                emit.effect(TripDetailsEffect.ShowSnackbar(R.string.trip_details_reorder_failed))
            }
        }
    }

    private fun resolveInitialSelectedDate(trip: Trip, nowDate: LocalDate): LocalDate {
        return when (
            getTripDateStatus(
                startDate = trip.startDate,
                endDate = trip.endDate,
                nowDate = nowDate
            )
        ) {
            TripDateStatus.InProgress -> nowDate
            TripDateStatus.Upcoming,
            TripDateStatus.Ended -> trip.startDate
        }
    }
}
