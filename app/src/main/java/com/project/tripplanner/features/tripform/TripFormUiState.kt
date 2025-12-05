package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.ErrorState
import com.project.tripplanner.State
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class TripFormUiState : State {
    data object Loading : TripFormUiState()

    data class Form(
        val tripId: Long? = null,
        val destination: String = "",
        val startDateMillis: Long? = null,
        val endDateMillis: Long? = null,
        val isSingleDay: Boolean = false,
        val notes: String = "",
        val coverImageUri: Uri? = null,
        val destinationError: String? = null,
        val startDateError: String? = null,
        val endDateError: String? = null,
        val isSaving: Boolean = false,
        val showStartDatePicker: Boolean = false,
        val showEndDatePicker: Boolean = false
    ) : TripFormUiState() {

        val startDate: String
            get() = startDateMillis?.formatAsDisplayDate() ?: ""

        val endDate: String
            get() = endDateMillis?.formatAsDisplayDate() ?: ""

        val isEditMode: Boolean
            get() = tripId != null

        val isSaveEnabled: Boolean
            get() = destination.isNotBlank() &&
                    startDateMillis != null &&
                    endDateMillis != null &&
                    startDateMillis <= endDateMillis &&
                    !isSaving
    }

    data class GlobalError(val errorState: ErrorState) : TripFormUiState()
}

private fun Long.formatAsDisplayDate(): String {
    val localDate = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
}
