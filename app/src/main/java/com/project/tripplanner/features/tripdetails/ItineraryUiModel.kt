package com.project.tripplanner.features.tripdetails

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.project.tripplanner.data.model.ItineraryType

@Immutable
data class ItineraryUiModel(
    val id: String,
    val title: String,
    @StringRes val categoryLabelResId: Int,
    val durationText: String,
    val type: ItineraryType,
    val locationQuery: String?,
    val hasMap: Boolean,
    val hasDocs: Boolean
)