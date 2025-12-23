package com.project.tripplanner.utils.time

import java.time.LocalDate

enum class TripDateStatus {
    Upcoming,
    InProgress,
    Ended
}

fun getTripDateStatus(
    startDate: LocalDate,
    endDate: LocalDate,
    nowDate: LocalDate
): TripDateStatus {
    return when {
        nowDate.isBefore(startDate) -> TripDateStatus.Upcoming
        nowDate.isAfter(endDate) -> TripDateStatus.Ended
        else -> TripDateStatus.InProgress
    }
}
