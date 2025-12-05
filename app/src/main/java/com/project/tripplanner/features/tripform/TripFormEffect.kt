package com.project.tripplanner.features.tripform

import androidx.annotation.StringRes
import com.project.tripplanner.Effect

sealed interface TripFormEffect : Effect {
    data object NavigateBack : TripFormEffect
    data class NavigateToTripDetail(val tripId: Long) : TripFormEffect
    data class ShowSnackbar(@StringRes val messageResId: Int) : TripFormEffect
}
