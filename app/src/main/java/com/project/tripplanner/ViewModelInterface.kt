package com.project.tripplanner

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ViewModelInterface<EVENT : Event, STATE : State, EFFECT : Effect> {
    /**
     * StateFlow which allows to observe state changes
     * **/
    val state: StateFlow<STATE>

    /**
     * SharedFlow which allows to observe commands to be executed
     * **/
    val effect: Flow<EFFECT>

    /**
     * Method that emits events for the vm
     * **/
    fun emitEvent(event: EVENT)
}

interface Emitter<STATE, EFFECT> {
    fun <S : STATE> updatedState(transform: (currentState: S) -> S)
    val effect: (effect: EFFECT) -> Unit
    val state: (state: STATE) -> Unit
}

// Flexible event handler types to reduce boilerplate
typealias EventHandler<EVENT, STATE, EFFECT> = suspend (
    event: EVENT,
    emit: Emitter<STATE, EFFECT>,
) -> Unit

// Handler that only needs emit (no event data needed)
typealias SimpleEventHandler<STATE, EFFECT> = suspend (
    emit: Emitter<STATE, EFFECT>,
) -> Unit

// Handler that doesn't need emit (pure side effects like navigation)
typealias SideEffectEventHandler<EVENT> = suspend (
    event: EVENT,
) -> Unit

// Handler that needs neither event nor emit (simple actions)
typealias NoParamEventHandler = suspend () -> Unit

typealias ErrorHandler<ERROR, STATE, EFFECT> = suspend (
    exception: ERROR,
    emit: Emitter<STATE, EFFECT>,
) -> Unit

interface Event

interface State

interface Effect

class Unused : Effect