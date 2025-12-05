package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.R
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.time.ClockProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@HiltViewModel
class TripFormViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val clockProvider: ClockProvider
) : BaseViewModel<TripFormEvent, TripFormUiState, TripFormEffect>(
    initialState = TripFormUiState.Loading
) {
    init {
        addEventHandler<TripFormEvent.ScreenLoaded>(::onScreenLoaded)
        addEventHandler<TripFormEvent.DestinationChanged>(::onDestinationChanged)
        addEventHandler<TripFormEvent.StartDateClicked>(::onStartDateClicked)
        addEventHandler<TripFormEvent.EndDateClicked>(::onEndDateClicked)
        addEventHandler<TripFormEvent.StartDateSelected>(::onStartDateSelected)
        addEventHandler<TripFormEvent.EndDateSelected>(::onEndDateSelected)
        addEventHandler<TripFormEvent.DatePickerDismissed>(::onDatePickerDismissed)
        addEventHandler<TripFormEvent.SingleDayToggled>(::onSingleDayToggled)
        addEventHandler<TripFormEvent.NotesChanged>(::onNotesChanged)
        addEventHandler<TripFormEvent.CoverImageSelected>(::onCoverImageSelected)
        addEventHandler<TripFormEvent.SaveClicked>(::onSaveClicked)
        addEventHandler<TripFormEvent.BackClicked>(::onBackClicked)
        addErrorHandler(MviDefaultErrorHandler(TripFormUiState::GlobalError))
    }

    private suspend fun onScreenLoaded(
        event: TripFormEvent.ScreenLoaded,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        if (event.tripId != null && event.tripId > 0) {
            loadTripForEdit(event.tripId, emit)
        } else {
            emit.state(TripFormUiState.Form())
        }
    }

    private suspend fun loadTripForEdit(
        tripId: Long,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.state(TripFormUiState.Loading)
        val trip = tripRepository.observeTrip(tripId).firstOrNull()
        if (trip != null) {
            val startDateMillis = trip.startDate.atStartOfDay(trip.timezone).toInstant().toEpochMilli()
            val endDateMillis = trip.endDate.atStartOfDay(trip.timezone).toInstant().toEpochMilli()
            emit.state(
                TripFormUiState.Form(
                    tripId = trip.id,
                    destination = trip.destination,
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    isSingleDay = trip.startDate == trip.endDate,
                    notes = trip.notes.orEmpty(),
                    coverImageUri = trip.coverImageUri?.let(Uri::parse)
                )
            )
        } else {
            emit.state(TripFormUiState.Form())
            emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_load_error))
        }
    }

    private fun onDestinationChanged(
        event: TripFormEvent.DestinationChanged,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(
                destination = event.value,
                destinationErrorId = null
            )
        }
    }

    private fun onStartDateClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(showStartDatePicker = true)
        }
    }

    private fun onEndDateClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(showEndDatePicker = true)
        }
    }

    private fun onStartDateSelected(
        event: TripFormEvent.StartDateSelected,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            val newEndDate = if (currentState.isSingleDay) event.millis else currentState.endDateMillis
            currentState.copy(
                startDateMillis = event.millis,
                endDateMillis = newEndDate,
                startDateErrorId = null,
                endDateErrorId = null,
                showStartDatePicker = false
            )
        }
    }

    private fun onEndDateSelected(
        event: TripFormEvent.EndDateSelected,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(
                endDateMillis = event.millis,
                endDateErrorId = null,
                showEndDatePicker = false
            )
        }
    }

    private fun onDatePickerDismissed(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(
                showStartDatePicker = false,
                showEndDatePicker = false
            )
        }
    }

    private fun onSingleDayToggled(
        event: TripFormEvent.SingleDayToggled,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            val newEndDate = if (event.enabled && currentState.startDateMillis != null) {
                currentState.startDateMillis
            } else {
                currentState.endDateMillis
            }
            currentState.copy(
                isSingleDay = event.enabled,
                endDateMillis = newEndDate,
                endDateErrorId = null
            )
        }
    }

    private fun onNotesChanged(
        event: TripFormEvent.NotesChanged,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(notes = event.value)
        }
    }

    private fun onCoverImageSelected(
        event: TripFormEvent.CoverImageSelected,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(coverImageUri = event.uri)
        }
    }

    private suspend fun onSaveClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        val currentState = state.value as? TripFormUiState.Form ?: return

        val validationResult = TripFormValidator.validate(currentState)
        if (!validationResult.isValid) {
            emit.state(
                currentState.copy(
                    destinationErrorId = validationResult.destinationErrorId,
                    startDateErrorId = validationResult.startDateErrorId,
                    endDateErrorId = validationResult.endDateErrorId
                )
            )
            return
        }

        emit.updatedState<TripFormUiState.Form> { it.copy(isSaving = true) }

        try {
            val startDate = millisToLocalDate(currentState.startDateMillis!!, clockProvider.zoneId)
            val endDate = millisToLocalDate(currentState.endDateMillis!!, clockProvider.zoneId)

            val tripInput = TripInput(
                destination = currentState.destination.trim(),
                startDate = startDate,
                endDate = endDate,
                timezone = clockProvider.zoneId,
                coverImageUri = currentState.coverImageUri?.toString(),
                notes = currentState.notes.takeIf { it.isNotBlank() }
            )

            if (currentState.isEditMode) {
                val existingTrip = tripRepository.observeTrip(currentState.tripId!!).firstOrNull()
                if (existingTrip != null) {
                    val updatedTrip = existingTrip.copy(
                        destination = tripInput.destination,
                        startDate = tripInput.startDate,
                        endDate = tripInput.endDate,
                        coverImageUri = tripInput.coverImageUri,
                        notes = tripInput.notes
                    )
                    tripRepository.updateTrip(updatedTrip)
                    emit.effect(TripFormEffect.NavigateToTripDetail(existingTrip.id))
                } else {
                    emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_load_error))
                }
            } else {
                val tripId = tripRepository.createTrip(tripInput)
                emit.effect(TripFormEffect.NavigateToTripDetail(tripId))
            }
        } catch (e: Exception) {
            emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_save_error))
        } finally {
            emit.updatedState<TripFormUiState.Form> { it.copy(isSaving = false) }
        }
    }

    private fun onBackClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.effect(TripFormEffect.NavigateBack)
    }

    private fun millisToLocalDate(millis: Long, zoneId: ZoneId): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(zoneId)
            .toLocalDate()
    }
}
