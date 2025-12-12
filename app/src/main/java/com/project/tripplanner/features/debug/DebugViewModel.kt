package com.project.tripplanner.features.debug

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.R
import com.project.tripplanner.repositories.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : BaseViewModel<DebugEvent, DebugUiState, DebugEffect>(DebugUiState()) {

    init {
        addEventHandler<DebugEvent.DeleteAllTripsClicked>(::onDeleteAllTripsClicked)
        addEventHandler<DebugEvent.MarkAllTripsEndedClicked>(::onMarkAllTripsEndedClicked)
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
        }
    }

    private fun performAction(
        emit: Emitter<DebugUiState, DebugEffect>,
        @StringRes successMessage: Int,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            emit.updatedState<DebugUiState> { current -> current.copy(isProcessing = true) }
            try {
                block()
                emit.effect(DebugEffect.ShowMessage(successMessage))
            } finally {
                emit.updatedState<DebugUiState> { current -> current.copy(isProcessing = false) }
            }
        }
    }
}
