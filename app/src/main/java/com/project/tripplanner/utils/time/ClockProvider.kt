package com.project.tripplanner.utils.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface ClockProvider {
    val zoneId: ZoneId
    fun now(): ZonedDateTime
    fun nowInstant(): Instant
}

@Singleton
class DefaultClockProvider @Inject constructor(
    private val clock: Clock = Clock.systemDefaultZone()
) : ClockProvider {
    override val zoneId: ZoneId get() = clock.zone

    override fun now(): ZonedDateTime = ZonedDateTime.now(clock)

    override fun nowInstant(): Instant = clock.instant()
}
