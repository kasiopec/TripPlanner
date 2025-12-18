package com.project.tripplanner.features.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.project.tripplanner.Effect
import com.project.tripplanner.ErrorState
import com.project.tripplanner.Event
import com.project.tripplanner.State

@Immutable
data class HomeUiState(
    val isInitialLoading: Boolean = true,
    val error: ErrorState? = null,
    val trips: List<TripUiModel> = emptyList(),
    val currentTrip: TripUiModel? = null,
    val countdownTrip: TripUiModel? = null,
    val listTrips: List<TripUiModel> = emptyList(),
    val activeFilter: HomeFilterType = HomeFilterType.All
) : State

sealed interface HomeEvent : Event {
    data object ScreenLoaded : HomeEvent
    data object RetryClicked : HomeEvent
    data class TripClicked(val tripId: Long) : HomeEvent
    data class FilterSelected(val filter: HomeFilterType) : HomeEvent
}

sealed interface HomeEffect : Effect {
    data class NavigateToTripDetail(val tripId: Long) : HomeEffect
    data class ShowSnackbar(@StringRes val messageResId: Int) : HomeEffect
}
