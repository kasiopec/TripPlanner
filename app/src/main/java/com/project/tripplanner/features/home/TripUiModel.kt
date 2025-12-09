package com.project.tripplanner.features.home

import android.net.Uri
import androidx.annotation.StringRes
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
    @StringRes val statusLabelResId: Int,
    val coverImageUri: Uri?,
    val progress: TripProgress? = null
)

