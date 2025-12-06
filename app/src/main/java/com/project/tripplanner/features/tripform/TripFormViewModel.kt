package com.project.tripplanner.features.tripform

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.MviDefaultErrorHandler
import com.project.tripplanner.R
import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.features.tripform.domain.CreateTripUseCase
import com.project.tripplanner.features.tripform.domain.TripFormValidator
import com.project.tripplanner.features.tripform.domain.UpdateTripUseCase
import com.project.tripplanner.repositories.TripRepository
import com.project.tripplanner.utils.time.ClockProvider
import com.project.tripplanner.utils.time.millisToLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class TripFormViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val createTripUseCase: CreateTripUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val tripCoverImageStorage: TripCoverImageStorage,
    private val clockProvider: ClockProvider,
    private val tripFormValidator: TripFormValidator
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
            emit.state(TripFormUiState.Form().updateSaveEnabled())
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
            val displayCoverUri = trip.coverImageUri?.let { tripCoverImageStorage.resolveForDisplay(it) }
            emit.state(
                TripFormUiState.Form(
                    tripId = trip.id,
                    destination = trip.destination,
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    isSingleDay = trip.startDate == trip.endDate,
                    notes = trip.notes.orEmpty(),
                    coverImagePath = trip.coverImageUri.takeIf { displayCoverUri != null },
                    coverImageUri = displayCoverUri,
                    pendingCoverImageUri = null
                ).updateSaveEnabled()
            )
            if (trip.coverImageUri != null && displayCoverUri == null) {
                emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_cover_missing_error))
            }
        } else {
            emit.state(TripFormUiState.Form().updateSaveEnabled())
            emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_load_error))
        }
    }

    private fun onDestinationChanged(
        event: TripFormEvent.DestinationChanged,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updateForm { currentState ->
            currentState.copy(
                destination = event.value,
                destinationErrorId = null
            )
        }
    }

    private fun onStartDateClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updateForm { currentState ->
            currentState.copy(showStartDatePicker = true)
        }
    }

    private fun onEndDateClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updateForm { currentState ->
            currentState.copy(showEndDatePicker = true)
        }
    }

    private fun onStartDateSelected(
        event: TripFormEvent.StartDateSelected,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updateForm { currentState ->
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
        emit.updateForm { currentState ->
            currentState.copy(
                endDateMillis = event.millis,
                endDateErrorId = null,
                showEndDatePicker = false
            )
        }
    }

    private fun onDatePickerDismissed(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.updateForm { currentState ->
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
        emit.updateForm { currentState ->
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
        emit.updateForm { currentState ->
            currentState.copy(notes = event.value)
        }
    }

    private fun onCoverImageSelected(
        event: TripFormEvent.CoverImageSelected,
        emit: Emitter<TripFormUiState, TripFormEffect>
    ) {
        emit.updateForm { currentState ->
            currentState.copy(
                pendingCoverImageUri = event.uri,
                coverImageUri = event.uri
            )
        }
    }

    private suspend fun onSaveClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        val currentState = state.value as? TripFormUiState.Form ?: return

        val validationResult = tripFormValidator.validate(
            currentState.destination,
            currentState.startDateMillis,
            currentState.endDateMillis
        )
        if (!validationResult.isValid) {
            emit.state(
                currentState.copy(
                    destinationErrorId = validationResult.destinationErrorId,
                    startDateErrorId = validationResult.startDateErrorId,
                    endDateErrorId = validationResult.endDateErrorId
                ).updateSaveEnabled()
            )
            return
        }

        emit.updateForm { it.copy(isSaving = true) }

        var storedCoverPath: String? = null
        val importedFromPicker = currentState.pendingCoverImageUri != null

        try {
            storedCoverPath = currentState.pendingCoverImageUri?.let { pickerUri ->
                tripCoverImageStorage.importFromPicker(pickerUri)
            } ?: currentState.coverImagePath?.takeIf { it.isNotBlank() }

            val resolvedCoverUri = storedCoverPath?.let { tripCoverImageStorage.resolveForDisplay(it) }

            val startDate = millisToLocalDate(currentState.startDateMillis!!, clockProvider.zoneId)
            val endDate = millisToLocalDate(currentState.endDateMillis!!, clockProvider.zoneId)

            val tripInput = TripInput(
                destination = currentState.destination.trim(),
                startDate = startDate,
                endDate = endDate,
                timezone = clockProvider.zoneId,
                coverImageUri = storedCoverPath,
                notes = currentState.notes.takeIf { it.isNotBlank() }
            )

            if (currentState.isEditMode) {
                val existingTrip = tripRepository.observeTrip(currentState.tripId!!).firstOrNull()
                if (existingTrip != null) {
                    updateTripUseCase(existingTrip, tripInput)
                    emit.updateForm {
                        it.copy(
                            coverImagePath = storedCoverPath,
                            pendingCoverImageUri = null,
                            coverImageUri = resolvedCoverUri ?: it.coverImageUri
                        )
                    }
                    emit.effect(TripFormEffect.NavigateToTripDetail(existingTrip.id))
                } else {
                    emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_load_error))
                }
            } else {
                val tripId = createTripUseCase(tripInput)
                emit.updateForm {
                    it.copy(
                        coverImagePath = storedCoverPath,
                        pendingCoverImageUri = null,
                        coverImageUri = resolvedCoverUri ?: it.coverImageUri
                    )
                }
                emit.effect(TripFormEffect.NavigateToTripDetail(tripId))
            }
        } catch (e: Exception) {
            if (importedFromPicker) {
                storedCoverPath?.let { runCatching { tripCoverImageStorage.delete(it) } }
            }
            emit.effect(TripFormEffect.ShowSnackbar(R.string.trip_form_save_error))
        } finally {
            emit.updateForm { it.copy(isSaving = false) }
        }
    }

    private fun onBackClicked(emit: Emitter<TripFormUiState, TripFormEffect>) {
        emit.effect(TripFormEffect.NavigateBack)
    }

    private fun TripFormUiState.Form.updateSaveEnabled(): TripFormUiState.Form {
        val result = tripFormValidator.validate(this.destination, this.startDateMillis, this.endDateMillis)
        return this.copy(isSaveEnabled = result.isValid && !this.isSaving)
    }

    private fun Emitter<TripFormUiState, TripFormEffect>.updateForm(
        block: (TripFormUiState.Form) -> TripFormUiState.Form
    ) {
        updatedState<TripFormUiState.Form> { currentState ->
            block(currentState).updateSaveEnabled()
        }
    }
}
