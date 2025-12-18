package com.project.tripplanner.features.home

import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.ErrorState
import com.project.tripplanner.repositories.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val tripUiMapper: HomeTripUiMapper
) : BaseViewModel<HomeEvent, HomeUiState, HomeEffect>(HomeUiState()) {

    private var tripsJob: Job? = null

    init {
        addEventHandler<HomeEvent.ScreenLoaded>(::onScreenLoaded)
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
            current.copy(
                activeFilter = event.filter,
                listTrips = tripUiMapper.filterListTrips(current.trips, event.filter)
            )
        }
    }

    private fun onTripClicked(event: HomeEvent.TripClicked, emit: Emitter<HomeUiState, HomeEffect>) {
        emit.effect(HomeEffect.NavigateToTripDetail(event.tripId))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObservingTrips(emit: Emitter<HomeUiState, HomeEffect>, showLoading: Boolean) {
        tripsJob?.cancel()
        if (showLoading) {
            emit.updatedState<HomeUiState> { current ->
                current.copy(isInitialLoading = true, error = null)
            }
        }
        tripsJob = viewModelScope.launch {
            tripRepository.observeTrips()
                .mapLatest { tripUiMapper.getTripUiModelList(it) }
                .catch { handleError(emit) }
                .collect { mapped ->
                    emit.updatedState<HomeUiState> { current ->
                        current.copy(
                            isInitialLoading = false,
                            error = null,
                            trips = mapped.trips,
                            currentTrip = mapped.currentTrip,
                            countdownTrip = mapped.countdownTrip,
                            listTrips = tripUiMapper.filterListTrips(mapped.trips, current.activeFilter)
                        )
                    }
                }
        }
    }

    private fun handleError(
        emit: Emitter<HomeUiState, HomeEffect>,
    ) {
        val hasTrips = state.value.trips.isNotEmpty()
        emit.updatedState<HomeUiState> { current ->
            if (hasTrips) {
                current
            } else {
                current.copy(
                    isInitialLoading = false,
                    error = ErrorState.UnknownError()
                )
            }
        }
    }
}
