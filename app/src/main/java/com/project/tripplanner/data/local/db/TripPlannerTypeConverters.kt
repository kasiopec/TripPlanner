package com.project.tripplanner.data.local.db

import androidx.room.TypeConverter
import com.project.tripplanner.data.model.ItineraryType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TripPlannerTypeConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.format(dateFormatter)

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it, dateFormatter) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.format(timeFormatter)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it, timeFormatter) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromItineraryType(value: ItineraryType?): String? = value?.name

    @TypeConverter
    fun toItineraryType(value: String?): ItineraryType? = value?.let(ItineraryType::valueOf)
}
