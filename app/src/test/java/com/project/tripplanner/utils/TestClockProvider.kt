package com.project.tripplanner.utils

import com.project.tripplanner.utils.time.ClockProvider
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class TestClockProvider(initial: ZonedDateTime) : ClockProvider {
    private var current = initial

    override val zoneId: ZoneId get() = current.zone

    override fun now(): ZonedDateTime = current

    override fun nowInstant(): Instant = current.toInstant()

    fun setNow(newNow: ZonedDateTime) {
        current = newNow
    }

    fun advanceBy(duration: Duration) {
        current = current.plus(duration)
    }
}
