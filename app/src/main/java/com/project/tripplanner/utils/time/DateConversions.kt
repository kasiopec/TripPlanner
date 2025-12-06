package com.project.tripplanner.utils.time

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun millisToLocalDate(millis: Long, zoneId: ZoneId): LocalDate {
    return Instant.ofEpochMilli(millis)
        .atZone(zoneId)
        .toLocalDate()
}
