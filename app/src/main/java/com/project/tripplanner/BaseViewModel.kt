package com.project.tripplanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.tripplanner.navigation.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

open class BaseViewModel<EVENT : Event, STATE : State, EFFECT : Effect> @Inject constructor(
    initialState: STATE
) : ViewModelInterface<EVENT, STATE, EFFECT>, ViewModel() {
    private val viewModelHandlers = mutableMapOf<String, EventHandler<EVENT, STATE, EFFECT>>()
    val errorHandlers = mutableMapOf<KClass<*>, ErrorHandler<*, STATE, EFFECT>>()
    private val events = Channel<EVENT>(Channel.BUFFERED).also {
        it.receiveAsFlow()
            .onEach(::toEvent)
            .launchIn(viewModelScope)
    }

    private val emitter: Emitter<STATE, EFFECT> = object : Emitter<STATE, EFFECT> {
        override fun <S : STATE> updatedState(transform: (currentState: S) -> S) {
            viewModelScope.launch {
                _state.emit(transform(_state.value as S))
            }
        }

        override val effect: (effect: EFFECT) -> Unit = { effect: EFFECT ->
            viewModelScope.launch { _effect.send(effect) }
        }
        override val state: (state: STATE) -> Unit = { state: STATE ->
            viewModelScope.launch { _state.emit(state) }
        }
        override val navigate: (navigation: NavigationEvent) -> Unit = { navigation: NavigationEvent ->
            viewModelScope.launch { _navigationEventChannel.send(navigation) }
        }
    }

    private suspend fun toEvent(event: EVENT) {
        try {
            val eventType = event::class.simpleName
                ?: throw IllegalArgumentException("Anonymous objects not supported")
            val handler = viewModelHandlers[eventType]
                ?: throw IllegalStateException("the event $event was fired without a handler to handle it")
            handler(event, emitter)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.w("Error caught in ViewModel ${this::class.simpleName}", e)
            mapException(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : EVENT> addEventHandler(noinline handler: EventHandler<T, STATE, EFFECT>) {
        handler as? EventHandler<EVENT, STATE, EFFECT>
            ?: throw IllegalArgumentException("Event handler must have the right type")
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        mapHandler(name, handler)
    }

    inline fun <reified T : EVENT> addEventHandlerWithoutEvent(noinline handler: NoEventEventHandler<STATE, EFFECT>) {
        val eventHandler: EventHandler<EVENT, STATE, EFFECT> = { _: EVENT, emit: Emitter<STATE, EFFECT> ->
            handler(emit)
        }
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        mapHandler(name, eventHandler)
    }

    fun mapHandler(name: String, handler: EventHandler<EVENT, STATE, EFFECT>) {
        if (viewModelHandlers.containsKey(name)) {
            throw IllegalStateException("only one handler per event can be registered for $name")
        }

        viewModelHandlers[name] = handler
    }

    inline fun <reified ERROR : Exception> addErrorHandler(errorHandler: MviErrorHandler<STATE, EFFECT, ERROR>) {
        addErrorHandler(errorHandler::handleError)
    }

    inline fun <reified ERROR : Exception> addErrorHandler(noinline handler: ErrorHandler<ERROR, STATE, EFFECT>) {
        errorHandlers[ERROR::class] = handler
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun mapException(e: Throwable) {
        val exception = e as? Exception ?: Exception(e)

        val errorHandler = errorHandlers.firstNotNullOfOrNull { (exceptionClass, errorHandler) ->
            errorHandler.takeIf {
                exceptionClass.isInstance(exception)
            }
        } as? ErrorHandler<Exception, STATE, EFFECT>

        errorHandler?.invoke(exception, emitter)
    }

    private val _state = MutableStateFlow(initialState)
    private val _effect = Channel<EFFECT>()
    private val _navigationEventChannel = Channel<NavigationEvent>()

    val navigationEvent = _navigationEventChannel.receiveAsFlow()

    override val state: StateFlow<STATE> = _state.asStateFlow()
    override val effect: Flow<EFFECT> = _effect.receiveAsFlow()

    override fun emitEvent(event: EVENT) {
        val isDelivered = events.trySend(event).isSuccess
        if (!isDelivered) {
            throw IllegalStateException("Missed $event on send, check UI emit logic")
        }
    }

    fun navigate(event: NavigationEvent) {
        viewModelScope.launch {
            _navigationEventChannel.send(event)
        }
    }

    inline fun <reified S : STATE> ViewModelInterface<*, S, *>.updateState(onUpdate: (S) -> S) {
        if (state is MutableStateFlow<S>) {
            (state as MutableStateFlow<S>).update { onUpdate(it) }
        } else {
            // Handle the case where state is not a MutableStateFlow<S> if needed
        }
    }
}

typealias NoEventEventHandler<STATE, EFFECT> = suspend (emit: Emitter<STATE, EFFECT>) -> Unit