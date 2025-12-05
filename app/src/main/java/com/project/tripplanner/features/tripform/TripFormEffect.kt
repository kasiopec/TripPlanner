package com.project.tripplanner.features.tripform

import com.project.tripplanner.Effect

sealed interface TripFormEffect : Effect {
    data object NavigateBack : TripFormEffect
    data class NavigateToTripDetail(val tripId: Long) : TripFormEffect
    data class ShowSnackbar(val message: String) : TripFormEffect
}
