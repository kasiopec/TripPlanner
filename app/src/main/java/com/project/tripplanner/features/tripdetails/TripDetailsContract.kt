package com.project.tripplanner.features.tripdetails

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.project.tripplanner.Effect
import com.project.tripplanner.ErrorState
import com.project.tripplanner.Event
import com.project.tripplanner.State
import com.project.tripplanner.ui.components.DayItem
import com.project.tripplanner.ui.components.ItineraryUiModel
import java.time.LocalDate

@Immutable
data class TripDetailsUiState(
    val isInitialLoading: Boolean = true,
    val error: ErrorState? = null,
    val tripTitle: String = "",
    val tripDateRange: String = "",
    @get:StringRes val tripStatusLabelResId: Int? = null,
    val days: List<DayItem> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val itinerary: List<ItineraryUiModel> = emptyList(),
    val isReorderMode: Boolean = false
) : State

sealed interface TripDetailsEvent : Event {
    data object ScreenLoaded : TripDetailsEvent
    data object RetryClicked : TripDetailsEvent
    data class DaySelected(val date: LocalDate) : TripDetailsEvent
    data object AddPlacesClicked : TripDetailsEvent
    data object ReorderClicked : TripDetailsEvent
    data class DoneClicked(val orderedIds: List<String>) : TripDetailsEvent
    data object BackClicked : TripDetailsEvent
}

sealed interface TripDetailsEffect : Effect {
    data object NavigateBack : TripDetailsEffect
    data class NavigateToActivityForm(val tripId: Long, val date: LocalDate) : TripDetailsEffect
    data class ShowSnackbar(@get:StringRes val messageResId: Int) : TripDetailsEffect
}
