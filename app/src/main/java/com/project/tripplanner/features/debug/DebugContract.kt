package com.project.tripplanner.features.debug

import androidx.annotation.StringRes
import com.project.tripplanner.Effect
import com.project.tripplanner.Event
import com.project.tripplanner.State

data class DebugUiState(
    val isProcessing: Boolean = false
): State

sealed interface DebugEvent : Event {
    data object DeleteAllTripsClicked : DebugEvent
    data object MarkAllTripsEndedClicked : DebugEvent
    data object AddTenPlacesToCurrentTripClicked : DebugEvent
    data object DeleteTenPlacesFromCurrentTripClicked : DebugEvent
}

sealed interface DebugEffect : Effect {
    data class ShowMessage(@StringRes val messageResId: Int) : DebugEffect
}
