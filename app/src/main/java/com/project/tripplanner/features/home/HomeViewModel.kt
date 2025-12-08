package com.project.tripplanner.features.home

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.ErrorState
import com.project.tripplanner.R
import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.time.ClockProvider
import com.project.tripplanner.utils.time.Countdown
import com.project.tripplanner.utils.time.CountdownFormatter
import com.project.tripplanner.utils.time.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val clockProvider: ClockProvider,
    private val countdownFormatter: CountdownFormatter,
    private val tripCoverImageStorage: TripCoverImageStorage,
    private val dateFormatter: DateFormatter
) : BaseViewModel<HomeEvent, HomeUiState, HomeEffect>(HomeUiState()) {

    private var tripsJob: Job? = null

    init {
        addEventHandler<HomeEvent.ScreenLoaded>(::onScreenLoaded)
        addEventHandler<HomeEvent.RefreshRequested>(::onRefreshRequested)
        addEventHandler<HomeEvent.RetryClicked>(::onRetryClicked)
        addEventHandler<HomeEvent.TripClicked>(::onTripClicked)
        addEventHandler<HomeEvent.FilterSelected>(::onFilterSelected)
    }

    private fun onScreenLoaded(event: HomeEvent.ScreenLoaded, emit: Emitter<HomeUiState, HomeEffect>) {
        startObservingTrips(emit, showLoading = true)
    }

    private fun onRetryClicked(event: HomeEvent.RetryClicked, emit: Emitter<HomeUiState, HomeEffect>) {
        startObservingTrips(emit, showLoading = true)
    }

    private fun onFilterSelected(event: HomeEvent.FilterSelected, emit: Emitter<HomeUiState, HomeEffect>) {
        emit.updatedState<HomeUiState> { current ->
            current.copy(activeFilter = event.filter)
        }
    }

    private fun onTripClicked(event: HomeEvent.TripClicked, emit: Emitter<HomeUiState, HomeEffect>) {
        emit.effect(HomeEffect.NavigateToTripDetail(event.tripId))
    }

    private suspend fun onRefreshRequested(event: HomeEvent.RefreshRequested, emit: Emitter<HomeUiState, HomeEffect>) {
        emit.updatedState<HomeUiState> { current ->
            current.copy(isRefreshing = true, error = null)
        }

        try {
            val trips = tripRepository.observeTrips().first()
            val mapped = mapTrips(trips)
            emit.updatedState<HomeUiState> { current ->
                current.copy(
                    isInitialLoading = false,
                    isRefreshing = false,
                    error = null,
                    trips = mapped.trips,
                    currentTripId = mapped.currentTripId,
                    countdown = mapped.countdown,
                    countdownTripId = mapped.countdownTripId
                )
            }
        } catch (e: Exception) {
            handleError(emit, isRefresh = true)
        } finally {
            emit.updatedState<HomeUiState> { current -> current.copy(isRefreshing = false) }
        }
    }

    private fun startObservingTrips(emit: Emitter<HomeUiState, HomeEffect>, showLoading: Boolean) {
        tripsJob?.cancel()
        if (showLoading) {
            emit.updatedState<HomeUiState> { current ->
                current.copy(isInitialLoading = true, error = null)
            }
        }
        tripsJob = viewModelScope.launch {
            tripRepository.observeTrips()
                .mapLatest { mapTrips(it) }
                .catch { handleError(emit, isRefresh = false) }
                .collect { mapped ->
                    emit.updatedState<HomeUiState> { current ->
                        current.copy(
                            isInitialLoading = false,
                            isRefreshing = false,
                            error = null,
                            trips = mapped.trips,
                            currentTripId = mapped.currentTripId,
                            countdown = mapped.countdown,
                            countdownTripId = mapped.countdownTripId
                        )
                    }
                }
        }
    }

    private suspend fun handleError(
        emit: Emitter<HomeUiState, HomeEffect>,
        isRefresh: Boolean
    ) {
        val hasTrips = state.value.trips.isNotEmpty()
        if (isRefresh && hasTrips) {
            emit.effect(HomeEffect.ShowSnackbar(R.string.error_unknown_message))
        }
        emit.updatedState<HomeUiState> { current ->
            if (hasTrips) {
                current.copy(isRefreshing = false)
            } else {
                current.copy(
                    isInitialLoading = false,
                    isRefreshing = false,
                    error = ErrorState.UnknownError()
                )
            }
        }
    }

    private suspend fun mapTrips(trips: List<Trip>): HomeMappedState {
        val nowDate = clockProvider.now().toLocalDate()
        val mappedTrips = trips.map { trip ->
            val status = classifyStatus(trip, nowDate)
            val progress = if (status == TripStatusUi.InProgress) {
                calculateProgress(trip, nowDate)
            } else {
                null
            }
            TripUiModel(
                id = trip.id,
                destination = trip.destination,
                startDate = trip.startDate,
                endDate = trip.endDate,
                timezone = trip.timezone,
                dateRangeText = dateFormatter.formatDateRange(trip.startDate, trip.endDate),
                status = status,
                statusLabelResId = status.labelResId,
                coverImageUri = tripCoverImageStorage.resolveForDisplay(trip.coverImageUri),
                progress = progress
            )
        }

        val sortedTrips = mappedTrips.sortedWith(
            compareBy<TripUiModel> { statusOrder(it.status) }
                .thenBy { it.startDate }
                .thenBy { it.id }
        )

        val currentTripId = sortedTrips.firstOrNull { it.status == TripStatusUi.InProgress }?.id

        val countdownData = if (currentTripId == null) {
            val nextUpcoming = sortedTrips
                .filter { it.status == TripStatusUi.None }
                .minByOrNull { it.startDate }
            val countdown = nextUpcoming?.let { trip ->
                countdownFormatter.countdownTo(trip.startDate).takeUnless { it.isExpired }
            }
            CountdownSelection(
                tripId = nextUpcoming?.id?.takeIf { countdown != null },
                countdown = countdown
            )
        } else {
            CountdownSelection(null, null)
        }

        return HomeMappedState(
            trips = sortedTrips,
            currentTripId = currentTripId,
            countdownTripId = countdownData.tripId,
            countdown = countdownData.countdown
        )
    }

    private fun classifyStatus(trip: Trip, nowDate: LocalDate): TripStatusUi {
        return when {
            nowDate.isBefore(trip.startDate) -> TripStatusUi.None
            nowDate.isAfter(trip.endDate) -> TripStatusUi.Ended
            else -> TripStatusUi.InProgress
        }
    }

    private fun calculateProgress(trip: Trip, nowDate: LocalDate): TripProgress? {
        val totalDays = ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
        if (totalDays <= 0) return null
        val currentDay = ChronoUnit.DAYS.between(trip.startDate, nowDate).toInt() + 1
        return TripProgress(
            currentDay = currentDay.coerceIn(1, totalDays),
            totalDays = totalDays
        )
    }

    private fun statusOrder(status: TripStatusUi): Int = when (status) {
        TripStatusUi.InProgress -> 0
        TripStatusUi.None -> 1
        TripStatusUi.Ended -> 2
    }
}

private data class HomeMappedState(
    val trips: List<TripUiModel>,
    val currentTripId: Long?,
    val countdownTripId: Long?,
    val countdown: Countdown?
)

private data class CountdownSelection(
    val tripId: Long?,
    val countdown: Countdown?
)
