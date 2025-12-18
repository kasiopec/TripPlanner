package com.project.tripplanner.features.home

import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.utils.time.ClockProvider
import com.project.tripplanner.utils.time.DateFormatter
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class HomeTripUiMapper @Inject constructor(
    private val clockProvider: ClockProvider,
    private val tripCoverImageStorage: TripCoverImageStorage,
    private val dateFormatter: DateFormatter,
) {
    suspend fun getTripUiModelList(
        trips: List<Trip>,
    ): HomeTripsUi {
        val nowDate = clockProvider.now().toLocalDate()
        val mappedTrips = trips.map { trip ->
            val status = getTripStatusUi(trip, nowDate)
            val progress = if (status == TripStatusUi.InProgress) {
                getTripProgress(trip, nowDate)
            } else {
                null
            }

            TripUiModel(
                id = trip.id,
                destination = trip.destination,
                startDate = trip.startDate,
                endDate = trip.endDate,
                timezone = trip.timezone,
                dateRangeText = dateFormatter.formatDateRange(trip.startDate, trip.endDate),
                status = status,
                coverImageUri = tripCoverImageStorage.resolveForDisplay(trip.coverImageUri),
                progress = progress,
            )
        }

        val sortedTrips = mappedTrips.sortedWith(
            compareBy<TripUiModel> { getTripSortOrderByStatus(it.status) }
                .thenBy { it.startDate }
                .thenBy { it.id },
        )

        val currentTrip = sortedTrips.firstOrNull { it.status == TripStatusUi.InProgress }
        val countdownTrip = if (currentTrip == null) {
            sortedTrips.firstOrNull { it.status == TripStatusUi.None }
        } else {
            null
        }

        return HomeTripsUi(
            trips = sortedTrips,
            currentTrip = currentTrip,
            countdownTrip = countdownTrip,
        )
    }

    fun filterListTrips(
        trips: List<TripUiModel>,
        activeFilter: HomeFilterType,
    ): List<TripUiModel> {
        val baseTrips = trips.filter { it.status != TripStatusUi.InProgress }
        return when (activeFilter) {
            HomeFilterType.All -> baseTrips
            HomeFilterType.Upcoming -> baseTrips.filter { it.status == TripStatusUi.None }
            HomeFilterType.Ended -> baseTrips.filter { it.status == TripStatusUi.Ended }
        }
    }

    private fun getTripStatusUi(trip: Trip, nowDate: LocalDate): TripStatusUi {
        return when {
            nowDate.isBefore(trip.startDate) -> TripStatusUi.None
            nowDate.isAfter(trip.endDate) -> TripStatusUi.Ended
            else -> TripStatusUi.InProgress
        }
    }

    private fun getTripProgress(trip: Trip, nowDate: LocalDate): TripProgress? {
        val totalDays = ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
        if (totalDays <= 0) return null
        val currentDay = ChronoUnit.DAYS.between(trip.startDate, nowDate).toInt() + 1
        return TripProgress(
            currentDay = currentDay.coerceIn(1, totalDays),
            totalDays = totalDays,
        )
    }

    private fun getTripSortOrderByStatus(status: TripStatusUi): Int = when (status) {
        TripStatusUi.InProgress -> 0
        TripStatusUi.None -> 1
        TripStatusUi.Ended -> 2
    }
}

data class HomeTripsUi(
    val trips: List<TripUiModel>,
    val currentTrip: TripUiModel?,
    val countdownTrip: TripUiModel?,
)
