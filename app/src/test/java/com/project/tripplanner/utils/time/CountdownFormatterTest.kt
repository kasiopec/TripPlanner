package com.project.tripplanner.utils.time

import com.project.tripplanner.utils.TestClockProvider
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountdownFormatterTest {
    private val zone = ZoneId.of("UTC")
    private val clock = TestClockProvider(ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, zone))
    private val formatter = CountdownFormatter(clock)

    @Test
    fun daysMode_whenMoreThan24Hours() {
        val countdown = formatter.countdownTo(LocalDate.of(2025, 1, 3))
        assertFalse(countdown.isExpired)
        assertEquals(CountdownMode.DAYS, countdown.mode)
        assertEquals(2, countdown.days)
        assertEquals(0, countdown.hours)
        assertEquals(0, countdown.minutes)
    }

    @Test
    fun minutesMode_whenWithin24Hours() {
        clock.setNow(ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, zone))
        val countdown = formatter.countdownTo(LocalDate.of(2025, 1, 2))
        assertFalse(countdown.isExpired)
        assertEquals(CountdownMode.MINUTES, countdown.mode)
        assertEquals(0, countdown.days)
        assertEquals(14, countdown.hours)
        assertEquals(0, countdown.minutes)
    }

    @Test
    fun expired_whenPastStartDate() {
        clock.setNow(ZonedDateTime.of(2025, 1, 2, 9, 0, 0, 0, zone))
        val countdown = formatter.countdownTo(LocalDate.of(2025, 1, 1))
        assertTrue(countdown.isExpired)
        assertEquals(0, countdown.days)
        assertEquals(0, countdown.hours)
        assertEquals(0, countdown.minutes)
    }
}
