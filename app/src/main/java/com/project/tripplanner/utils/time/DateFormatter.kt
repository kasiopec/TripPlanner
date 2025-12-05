package com.project.tripplanner.utils.time

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
    fun formatAsDisplayDate(millis: Long?): String {
        if (millis == null) return ""
        val localDate = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
    }
}
