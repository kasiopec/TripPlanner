package com.project.tripplanner.data.local.db

import com.project.tripplanner.data.model.ItineraryType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TripPlannerTypeConvertersTest {
    private val converters = TripPlannerTypeConverters()

    @Test
    fun localDate_roundTrip() {
        val date = LocalDate.of(2025, 1, 5)
        val stored = converters.fromLocalDate(date)
        val restored = converters.toLocalDate(stored)
        assertEquals(date, restored)
    }

    @Test
    fun localTime_roundTrip() {
        val time = LocalTime.of(10, 15, 30)
        val stored = converters.fromLocalTime(time)
        val restored = converters.toLocalTime(stored)
        assertEquals(time, restored)
    }

    @Test
    fun instant_roundTrip() {
        val instant = Instant.parse("2025-01-01T12:00:00Z")
        val stored = converters.fromInstant(instant)
        val restored = converters.toInstant(stored)
        assertEquals(instant, restored)
    }

    @Test
    fun itineraryType_roundTrip() {
        val type = ItineraryType.Flight
        val stored = converters.fromItineraryType(type)
        val restored = converters.toItineraryType(stored)
        assertEquals(type, restored)
    }

    @Test
    fun converters_handleNull() {
        assertNull(converters.toLocalDate(null))
        assertNull(converters.toLocalTime(null))
        assertNull(converters.toInstant(null))
        assertNull(converters.toItineraryType(null))
    }
}
