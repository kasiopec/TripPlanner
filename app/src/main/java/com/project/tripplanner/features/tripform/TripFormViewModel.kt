package com.project.tripplanner.features.tripform

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.time.ClockProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

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

    private fun onScreenLoaded(
        event: TripFormEvent.ScreenLoaded,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        if (event.tripId != null && event.tripId > 0) {
            loadTripForEdit(event.tripId, emit)
        } else {
            emit.state(TripFormUiState.Form())
        }
    }

    private fun loadTripForEdit(
        tripId: Long,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.state(TripFormUiState.Form(tripId = tripId))
    }

    private fun onDestinationChanged(
        event: TripFormEvent.DestinationChanged,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updatedState<TripFormUiState.Form> { currentState ->
            currentState.copy(
                destination = event.value,
                destinationError = null
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
                startDateError = null,
                endDateError = null,
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
                endDateError = null,
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
                endDateError = null
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
        val currentState = state.value
        if (currentState !is TripFormUiState.Form) return

        val validationResult = validateForm(currentState)
        if (!validationResult.isValid) {
            emit.state(
                currentState.copy(
                    destinationError = validationResult.destinationError,
                    startDateError = validationResult.startDateError,
                    endDateError = validationResult.endDateError
                )
            )
            return
        }

        emit.state(currentState.copy(isSaving = true))

        try {
            val startDate = millisToLocalDate(currentState.startDateMillis!!)
            val endDate = millisToLocalDate(currentState.endDateMillis!!)

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
                    emit.effect(TripFormEffect.NavigateToTripDetail(currentState.tripId))
                } else {
                    emit.state(currentState.copy(isSaving = false))
                    emit.effect(TripFormEffect.ShowSnackbar("Trip not found"))
                }
            } else {
                val tripId = tripRepository.createTrip(tripInput)
                emit.effect(TripFormEffect.NavigateToTripDetail(tripId))
            }
        } catch (e: Exception) {
            emit.state(currentState.copy(isSaving = false))
            emit.effect(TripFormEffect.ShowSnackbar("Failed to save trip. Please try again."))
        }
    }

    private fun onBackClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.effect(TripFormEffect.NavigateBack)
    }

    private fun validateForm(state: TripFormUiState.Form): ValidationResult {
        val destinationError = if (state.destination.isBlank()) "Destination is required" else null
        val startDateError = if (state.startDateMillis == null) "Start date is required" else null
        val endDateError = when {
            state.endDateMillis == null -> "End date is required"
            state.startDateMillis != null && state.endDateMillis < state.startDateMillis ->
                "End date must be after start date"
            else -> null
        }

        return ValidationResult(
            isValid = destinationError == null && startDateError == null && endDateError == null,
            destinationError = destinationError,
            startDateError = startDateError,
            endDateError = endDateError
        )
    }

    private fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private data class ValidationResult(
        val isValid: Boolean,
        val destinationError: String?,
        val startDateError: String?,
        val endDateError: String?
    )
}
