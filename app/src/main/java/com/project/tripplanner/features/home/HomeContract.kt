package com.project.tripplanner.features.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.project.tripplanner.Effect
import com.project.tripplanner.ErrorState
import com.project.tripplanner.Event
import com.project.tripplanner.State
import com.project.tripplanner.utils.time.Countdown

@Immutable
data class HomeUiState(
    val isInitialLoading: Boolean = true,
    val error: ErrorState? = null,
    val trips: List<TripUiModel> = emptyList(),
    val currentTripId: Long? = null,
    val countdown: Countdown? = null,
    val countdownTripId: Long? = null,
    val activeFilter: HomeFilter = HomeFilter.All
) : State

sealed interface HomeEvent : Event {
    data object ScreenLoaded : HomeEvent
    data object RetryClicked : HomeEvent
    data class TripClicked(val tripId: Long) : HomeEvent
    data class FilterSelected(val filter: HomeFilter) : HomeEvent
}

sealed interface HomeEffect : Effect {
    data class NavigateToTripDetail(val tripId: Long) : HomeEffect
    data class ShowSnackbar(@StringRes val messageResId: Int) : HomeEffect
}
