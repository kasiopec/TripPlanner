package com.project.tripplanner.utils.time

import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

enum class CountdownMode { DAYS, MINUTES }

data class Countdown(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val mode: CountdownMode,
    val isExpired: Boolean
)

@Singleton
class CountdownFormatter @Inject constructor(
    private val clockProvider: ClockProvider
) {
    fun countdownTo(startDate: LocalDate): Countdown {
        val now = clockProvider.now()
        val target = startDate.atStartOfDay(clockProvider.zoneId)
        val duration = Duration.between(now, target)
        if (duration.isZero || duration.isNegative) {
            return Countdown(days = 0, hours = 0, minutes = 0, mode = CountdownMode.DAYS, isExpired = true)
        }

        val totalMinutes = duration.toMinutes()
        return if (totalMinutes >= MINUTES_PER_DAY) {
            val days = duration.toDays()
            val hours = duration.toHours() % HOURS_PER_DAY
            val minutes = totalMinutes % MINUTES_PER_HOUR
            Countdown(days = days, hours = hours, minutes = minutes, mode = CountdownMode.DAYS, isExpired = false)
        } else {
            val hours = duration.toHours()
            val minutes = totalMinutes % MINUTES_PER_HOUR
            Countdown(days = 0, hours = hours, minutes = minutes, mode = CountdownMode.MINUTES, isExpired = false)
        }
    }

    private companion object {
        const val HOURS_PER_DAY = 24
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR
    }
}
