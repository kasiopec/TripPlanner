package com.project.tripplanner.features.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.project.tripplanner.Effect
import com.project.tripplanner.ErrorState
import com.project.tripplanner.Event
import com.project.tripplanner.R
import com.project.tripplanner.State
import com.project.tripplanner.utils.time.Countdown
import java.time.LocalDate
import java.time.ZoneId

enum class HomeFilter(@StringRes val labelResId: Int) {
    All(R.string.home_filter_all),
    Upcoming(R.string.home_filter_upcoming),
    Ended(R.string.home_filter_ended)
}

enum class TripStatusUi(@StringRes val labelResId: Int) {
    None(R.string.home_status_upcoming),
    InProgress(R.string.trip_status_in_progress),
    Ended(R.string.trip_status_ended)
}

@Immutable
data class TripProgress(
    val currentDay: Int,
    val totalDays: Int
)

@Immutable
data class TripUiModel(
    val id: Long,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: ZoneId,
    val dateRangeText: String,
    val status: TripStatusUi,
    @StringRes val statusLabelResId: Int,
    val coverImageUri: Uri?,
    val progress: TripProgress? = null
)

@Immutable
data class HomeUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: ErrorState? = null,
    val trips: List<TripUiModel> = emptyList(),
    val currentTripId: Long? = null,
    val countdown: Countdown? = null,
    val countdownTripId: Long? = null,
    val activeFilter: HomeFilter = HomeFilter.All
) : State

sealed interface HomeEvent : Event {
    data object ScreenLoaded : HomeEvent
    data object RefreshRequested : HomeEvent
    data object RetryClicked : HomeEvent
    data class TripClicked(val tripId: Long) : HomeEvent
    data class FilterSelected(val filter: HomeFilter) : HomeEvent
}

sealed interface HomeEffect : Effect {
    data class NavigateToTripDetail(val tripId: Long) : HomeEffect
    data class ShowSnackbar(@StringRes val messageResId: Int) : HomeEffect
}
