package com.project.tripplanner.features.debug

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.R
import com.project.tripplanner.data.KEY_LAST_OPENED_TRIP_ID
import com.project.tripplanner.data.KEY_LAST_OPENED_TRIP_DATE
import com.project.tripplanner.data.UserPrefsStorage
import com.project.tripplanner.data.model.ItineraryItemInput
import com.project.tripplanner.data.model.ItineraryType
import com.project.tripplanner.repositories.ItineraryRepository
import com.project.tripplanner.repositories.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val DEBUG_PLACE_TITLE_PREFIX = "[DEBUG_PLACE] "
private const val DEBUG_PLACE_COUNT = 10
private const val DEBUG_PLACE_TIME_STEP_MINUTES = 15L
private val DEBUG_PLACE_LOCATIONS = listOf(
    "Colosseum, Rome",
    null,
    "1 Infinite Loop, Cupertino, CA",
    "Times Square, New York, NY",
    null,
    "Shibuya Crossing, Tokyo",
    "Eiffel Tower, Paris",
    null,
    "Sagrada Familia, Barcelona",
    "London Bridge, London"
)

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryRepository,
    private val userPrefsStorage: UserPrefsStorage
) : BaseViewModel<DebugEvent, DebugUiState, DebugEffect>(DebugUiState()) {

    init {
        addEventHandler<DebugEvent.DeleteAllTripsClicked>(::onDeleteAllTripsClicked)
        addEventHandler<DebugEvent.MarkAllTripsEndedClicked>(::onMarkAllTripsEndedClicked)
        addEventHandler<DebugEvent.AddTenPlacesToCurrentTripClicked>(::onAddTenPlacesToCurrentTripClicked)
        addEventHandler<DebugEvent.DeleteTenPlacesFromCurrentTripClicked>(::onDeleteTenPlacesFromCurrentTripClicked)
    }

    private fun onDeleteAllTripsClicked(
        event: DebugEvent.DeleteAllTripsClicked,
        emit: Emitter<DebugUiState, DebugEffect>
    ) {
        performAction(
            emit = emit,
            successMessage = R.string.debug_delete_success
        ) {
            tripRepository.deleteAllTrips()
            true
        }
    }

    private fun onMarkAllTripsEndedClicked(
        event: DebugEvent.MarkAllTripsEndedClicked,
        emit: Emitter<DebugUiState, DebugEffect>
    ) {
        performAction(
            emit = emit,
            successMessage = R.string.debug_mark_ended_success
        ) {
            tripRepository.markAllTripsEnded()
            true
        }
    }

    private fun onAddTenPlacesToCurrentTripClicked(
        event: DebugEvent.AddTenPlacesToCurrentTripClicked,
        emit: Emitter<DebugUiState, DebugEffect>
    ) {
        performAction(
            emit = emit,
            successMessage = R.string.debug_add_10_places_success
        ) {
            val currentTripId = userPrefsStorage.getLong(KEY_LAST_OPENED_TRIP_ID) ?: return@performAction false
            val currentTrip = tripRepository.getTrip(currentTripId) ?: return@performAction false
            val targetDate = resolveTargetDate(currentTrip.startDate, currentTrip.endDate)

            (1..DEBUG_PLACE_COUNT).forEach { index ->
                val time = LocalTime.of(9, 0).plusMinutes((index - 1) * DEBUG_PLACE_TIME_STEP_MINUTES)

                itineraryRepository.addItem(
                    ItineraryItemInput(
                        tripId = currentTrip.id,
                        localDate = targetDate,
                        localTime = time,
                        title = "$DEBUG_PLACE_TITLE_PREFIX Place $index",
                        type = ItineraryType.Activity,
                        location = resolveDebugLocation(index),
                        notes = null
                    )
                )
            }
            true
        }
    }

    private fun onDeleteTenPlacesFromCurrentTripClicked(
        event: DebugEvent.DeleteTenPlacesFromCurrentTripClicked,
        emit: Emitter<DebugUiState, DebugEffect>
    ) {
        performAction(
            emit = emit,
            successMessage = R.string.debug_delete_10_places_success
        ) {
            val currentTripId = userPrefsStorage.getLong(KEY_LAST_OPENED_TRIP_ID) ?: return@performAction false
            val currentTrip = tripRepository.getTrip(currentTripId) ?: return@performAction false
            val items = itineraryRepository.observeItinerary(currentTrip.id).firstOrNull().orEmpty()
            val debugItemIds = items
                .asSequence()
                .filter { it.title.startsWith(DEBUG_PLACE_TITLE_PREFIX) }
                .map { it.id }
                .toList()

            debugItemIds.forEach { itineraryRepository.deleteItem(it) }
            true
        }
    }

    private fun resolveTargetDate(startDate: LocalDate, endDate: LocalDate): LocalDate {
        val stored = userPrefsStorage.getString(KEY_LAST_OPENED_TRIP_DATE)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        if (stored != null && !stored.isBefore(startDate) && !stored.isAfter(endDate)) return stored
        return startDate
    }

    private fun resolveDebugLocation(index: Int): String? {
        val zeroBasedIndex = (index - 1).coerceAtLeast(0)
        return DEBUG_PLACE_LOCATIONS.getOrNull(zeroBasedIndex)
    }

    private fun performAction(
        emit: Emitter<DebugUiState, DebugEffect>,
        @StringRes successMessage: Int,
        block: suspend () -> Boolean
    ) {
        viewModelScope.launch {
            emit.updatedState<DebugUiState> { current -> current.copy(isProcessing = true) }
            try {
                val didPerform = block()
                if (didPerform) {
                    emit.effect(DebugEffect.ShowMessage(successMessage))
                }
            } finally {
                emit.updatedState<DebugUiState> { current -> current.copy(isProcessing = false) }
            }
        }
    }
}
