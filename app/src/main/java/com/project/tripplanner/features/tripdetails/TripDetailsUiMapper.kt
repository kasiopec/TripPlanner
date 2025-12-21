package com.project.tripplanner.features.tripdetails

import com.project.tripplanner.data.model.ItineraryItem
import com.project.tripplanner.ui.components.DayItem
import com.project.tripplanner.ui.components.ItineraryUiModel
import com.project.tripplanner.utils.time.DateFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class TripDetailsUiMapper @Inject constructor(
    private val dateFormatter: DateFormatter
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

    fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
        return dateFormatter.formatDateRange(startDate, endDate)
    }

    fun buildDayItems(
        startDate: LocalDate,
        endDate: LocalDate,
        selectedDate: LocalDate
    ): List<DayItem> {
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        return (0 until totalDays).map { index ->
            val date = startDate.plusDays(index.toLong())
            DayItem(
                dayIndex = index + 1,
                date = date,
                isSelected = date == selectedDate
            )
        }
    }

    fun updateSelectedDay(
        days: List<DayItem>,
        selectedDate: LocalDate
    ): List<DayItem> {
        return days.map { day -> day.copy(isSelected = day.date == selectedDate) }
    }

    fun buildItineraryUiModels(items: List<ItineraryItem>): List<ItineraryUiModel> {
        return items.map { item ->
            ItineraryUiModel(
                id = item.id.toString(),
                title = item.title,
                categoryName = item.type.name,
                durationText = formatTime(item.localTime),
                type = item.type,
                hasMap = !item.location.isNullOrBlank(),
                hasDocs = false
            )
        }
    }

    private fun formatTime(localTime: LocalTime?): String {
        return localTime?.format(timeFormatter).orEmpty()
    }
}
