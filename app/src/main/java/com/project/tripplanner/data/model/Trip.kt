package com.project.tripplanner.data.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class Trip(
    val id: Long,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: ZoneId,
    val coverImageUri: String?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class TripInput(
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: ZoneId,
    val coverImageUri: String?,
    val notes: String?
)
