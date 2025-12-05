package com.project.tripplanner.data.local.entity

import java.time.Instant
import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: String,
    val coverImageUri: String?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
