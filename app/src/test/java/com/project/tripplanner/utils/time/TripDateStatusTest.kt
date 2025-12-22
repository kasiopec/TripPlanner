package com.project.tripplanner.utils.time

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class TripDateStatusTest {
    private val startDate = LocalDate.of(2025, 5, 10)
    private val endDate = LocalDate.of(2025, 5, 12)

    @Test
    fun upcoming_whenBeforeStartDate() {
        val status = getTripDateStatus(
            startDate = startDate,
            endDate = endDate,
            nowDate = LocalDate.of(2025, 5, 9)
        )
        assertEquals(TripDateStatus.Upcoming, status)
    }

    @Test
    fun inProgress_whenOnStartDate() {
        val status = getTripDateStatus(
            startDate = startDate,
            endDate = endDate,
            nowDate = startDate
        )
        assertEquals(TripDateStatus.InProgress, status)
    }

    @Test
    fun inProgress_whenBetweenStartAndEnd() {
        val status = getTripDateStatus(
            startDate = startDate,
            endDate = endDate,
            nowDate = LocalDate.of(2025, 5, 11)
        )
        assertEquals(TripDateStatus.InProgress, status)
    }

    @Test
    fun inProgress_whenOnEndDate() {
        val status = getTripDateStatus(
            startDate = startDate,
            endDate = endDate,
            nowDate = endDate
        )
        assertEquals(TripDateStatus.InProgress, status)
    }

    @Test
    fun ended_whenAfterEndDate() {
        val status = getTripDateStatus(
            startDate = startDate,
            endDate = endDate,
            nowDate = LocalDate.of(2025, 5, 13)
        )
        assertEquals(TripDateStatus.Ended, status)
    }
}
