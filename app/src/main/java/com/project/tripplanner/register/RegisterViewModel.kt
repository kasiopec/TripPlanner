package com.project.tripplanner.register

import com.project.tripplanner.BaseViewModel
import com.project.tripplanner.Emitter
import com.project.tripplanner.Unused
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : BaseViewModel<RegisterEvent, RegisterUiState, Unused>(
    initialState = RegisterUiState.Loading
) {
    init {
        addEventHandler(::onScreenVisible)
        addEventHandler(::onRegisterClicked)
    }

    private fun onScreenVisible(
        event: RegisterEvent.ScreenVisibleEvent,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        emit.state(RegisterUiState.Register())
    }

    private fun onRegisterClicked(
        event: RegisterEvent.OnRegisterClickedEvent,
        emit: Emitter<RegisterUiState, Unused>
    ) {
        // validate stuff
    }
}