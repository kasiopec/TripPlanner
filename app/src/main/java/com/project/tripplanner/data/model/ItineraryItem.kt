package com.project.tripplanner.data.model

import java.time.LocalDate
import java.time.LocalTime

data class ItineraryItem(
    val id: Long,
    val tripId: Long,
    val localDate: LocalDate,
    val localTime: LocalTime?,
    val title: String,
    val type: ItineraryType,
    val location: String?,
    val notes: String?,
    val sortOrder: Long
)

data class ItineraryItemInput(
    val tripId: Long,
    val localDate: LocalDate,
    val localTime: LocalTime?,
    val title: String,
    val type: ItineraryType,
    val location: String?,
    val notes: String?,
    val sortOrder: Long? = null
)
