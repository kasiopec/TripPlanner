package com.project.tripplanner.utils.time

import java.time.LocalDate
import java.time.ZoneId

fun ClockProvider.nowLocalDate(zoneId: ZoneId = this.zoneId): LocalDate {
    return now().withZoneSameInstant(zoneId).toLocalDate()
}

