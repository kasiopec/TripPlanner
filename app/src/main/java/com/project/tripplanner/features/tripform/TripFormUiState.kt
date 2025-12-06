package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.ErrorState
import com.project.tripplanner.State

sealed class TripFormUiState : State {
    data object Loading : TripFormUiState()

    data class Form(
        val tripId: Long? = null,
        val destination: String = "",
        val startDateMillis: Long? = null,
        val endDateMillis: Long? = null,
        val isSingleDay: Boolean = false,
        val notes: String = "",
        val coverImagePath: String? = null,
        val coverImageUri: Uri? = null,
        val pendingCoverImageUri: Uri? = null,
        val destinationErrorId: Int? = null,
        val startDateErrorId: Int? = null,
        val endDateErrorId: Int? = null,
        val isSaving: Boolean = false,
        val isSaveEnabled: Boolean = false,
        val showStartDatePicker: Boolean = false,
        val showEndDatePicker: Boolean = false
    ) : TripFormUiState() {
        val isEditMode: Boolean
            get() = tripId != null
    }

    data class GlobalError(val errorState: ErrorState) : TripFormUiState()
}
