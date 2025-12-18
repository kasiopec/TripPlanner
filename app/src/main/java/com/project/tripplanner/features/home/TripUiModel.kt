package com.project.tripplanner.features.home

import android.net.Uri
import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.ZoneId

@Immutable
data class TripProgress(
    val currentDay: Int,
    val totalDays: Int
)

@Immutable
data class TripUiModel(
    val id: Long,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: ZoneId,
    val dateRangeText: String,
    val status: TripStatusUi,
    val coverImageUri: Uri?,
    val progress: TripProgress? = null
)
