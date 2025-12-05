package com.project.tripplanner.features.tripform.domain

import com.project.tripplanner.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripFormValidator @Inject constructor() {

    fun validate(
        destination: String,
        startDateMillis: Long?,
        endDateMillis: Long?
    ): TripFormValidationResult {
        val destinationErrorId = if (destination.isBlank()) R.string.trip_form_destination_error else null
        val startDateErrorId = if (startDateMillis == null) R.string.trip_form_start_date_error else null
        val endDateErrorId = when {
            endDateMillis == null -> R.string.trip_form_end_date_error
            startDateMillis != null && endDateMillis < startDateMillis -> R.string.trip_form_date_range_error
            else -> null
        }

        return TripFormValidationResult(
            isValid = destinationErrorId == null && startDateErrorId == null && endDateErrorId == null,
            destinationErrorId = destinationErrorId,
            startDateErrorId = startDateErrorId,
            endDateErrorId = endDateErrorId
        )
    }
}

data class TripFormValidationResult(
    val isValid: Boolean,
    val destinationErrorId: Int?,
    val startDateErrorId: Int?,
    val endDateErrorId: Int?
)
