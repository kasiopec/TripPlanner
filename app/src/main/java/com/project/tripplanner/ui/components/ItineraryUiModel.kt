package com.project.tripplanner.ui.components

import androidx.compose.runtime.Immutable
import com.project.tripplanner.data.model.ItineraryType

@Immutable
data class ItineraryUiModel(
    val id: String,
    val title: String,
    val categoryName: String,
    val durationText: String,
    val type: ItineraryType,
    val hasMap: Boolean,
    val hasDocs: Boolean
)
