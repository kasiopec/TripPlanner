package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.ErrorState
import com.project.tripplanner.R
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
        val destinationErrorId: Int? = null,
        val startDateErrorId: Int? = null,
        val endDateErrorId: Int? = null,
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
            get() = TripFormValidator.validate(this).isValid && !isSaving
    }

    data class GlobalError(val errorState: ErrorState) : TripFormUiState()
}

private fun Long.formatAsDisplayDate(): String {
    val localDate = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
}

data class ValidationResult(
    val isValid: Boolean,
    val destinationErrorId: Int?,
    val startDateErrorId: Int?,
    val endDateErrorId: Int?
)

object TripFormValidator {
    fun validate(state: TripFormUiState.Form): ValidationResult {
        val destinationErrorId =
            if (state.destination.isBlank()) R.string.trip_form_destination_error else null
        val startDateErrorId =
            if (state.startDateMillis == null) R.string.trip_form_start_date_error else null
        val endDateErrorId = when {
            state.endDateMillis == null -> R.string.trip_form_end_date_error
            state.startDateMillis != null && state.endDateMillis < state.startDateMillis ->
                R.string.trip_form_date_range_error

            else -> null
        }

        return ValidationResult(
            isValid = destinationErrorId == null && startDateErrorId == null && endDateErrorId == null,
            destinationErrorId = destinationErrorId,
            startDateErrorId = startDateErrorId,
            endDateErrorId = endDateErrorId
        )
    }
}
