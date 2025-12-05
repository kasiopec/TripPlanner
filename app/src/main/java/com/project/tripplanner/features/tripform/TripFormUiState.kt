package com.project.tripplanner.features.tripform

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class TripFormUiState(
    val destination: String = "",
    val destinationError: String? = null,
    val startDate: String = "",
    val startDateError: String? = null,
    val endDate: String = "",
    val endDateError: String? = null,
    val isSingleDay: Boolean = false,
    val coverImageUri: Uri? = null,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false
)
