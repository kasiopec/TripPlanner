package com.project.tripplanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.tripplanner.navigation.NavigationEffect
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

/**
 * Improved BaseViewModel with flexible event handler types to reduce boilerplate.
 *
 * Event Handler Types:
 * 1. EventHandler<EVENT, STATE, EFFECT> - Full handler with event data and emit capability
 * 2. SimpleEventHandler<STATE, EFFECT> - Only needs emit, no event data required
 * 3. SideEffectEventHandler<EVENT> - Only needs event data, for pure side effects (like navigation)
 * 4. NoParamEventHandler - Neither event nor emit needed, for simple actions
 *
 * Navigation is now handled via Effects instead of a separate navigation channel.
 * This provides better separation of concerns and makes testing easier.
 *
 * Usage Examples:
 * - addEventHandler(::onLoginClicked) // When you need both event data and emit
 * - addSimpleEventHandler<LoadDataEvent> { emit -> emit.state(LoadingState) } // No event data needed
 * - addSideEffectHandler<NavigateBackEvent> { navigate() } // Pure side effect
 * - addNoParamHandler<RefreshEvent> { refreshData() } // Simple action
 */
open class BaseViewModel<EVENT : Event, STATE : State, EFFECT : Effect> @Inject constructor(
    initialState: STATE
) : ViewModelInterface<EVENT, STATE, EFFECT>, ViewModel() {

    // Handler storage for different types of event handlers
    private val fullEventHandlers = mutableMapOf<String, EventHandler<EVENT, STATE, EFFECT>>()
    private val simpleEventHandlers = mutableMapOf<String, SimpleEventHandler<STATE, EFFECT>>()
    private val sideEffectHandlers = mutableMapOf<String, SideEffectEventHandler<EVENT>>()
    private val noParamHandlers = mutableMapOf<String, NoParamEventHandler>()

    val errorHandlers = mutableMapOf<KClass<*>, ErrorHandler<*, STATE, EFFECT>>()

    private val events = Channel<EVENT>(Channel.BUFFERED).also {
        it.receiveAsFlow()
            .onEach(::handleEvent)
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
    }

    private suspend fun handleEvent(event: EVENT) {
        try {
            val eventType = event::class.simpleName
                ?: throw IllegalArgumentException("Anonymous objects not supported")

            // Try different handler types in order of specificity
            when {
                fullEventHandlers.containsKey(eventType) -> {
                    fullEventHandlers[eventType]!!(event, emitter)
                }

                simpleEventHandlers.containsKey(eventType) -> {
                    simpleEventHandlers[eventType]!!(emitter)
                }

                sideEffectHandlers.containsKey(eventType) -> {
                    sideEffectHandlers[eventType]!!(event)
                }

                noParamHandlers.containsKey(eventType) -> {
                    noParamHandlers[eventType]!!()
                }

                else -> {
                    throw IllegalStateException("No handler found for event: $event")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.w("Error caught in ViewModel ${this::class.simpleName}", e)
            mapException(e)
        }
    }

    // Full event handler (current approach)
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : EVENT> addEventHandler(noinline handler: EventHandler<T, STATE, EFFECT>) {
        handler as? EventHandler<EVENT, STATE, EFFECT>
            ?: throw IllegalArgumentException("Event handler must have the right type")
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        registerHandler(name, handler)
    }

    // Simple event handler (only needs emit)
    inline fun <reified T : EVENT> addSimpleEventHandler(noinline handler: SimpleEventHandler<STATE, EFFECT>) {
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        registerSimpleHandler(name, handler)
    }

    // Side effect handler (only needs event, like for navigation)
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : EVENT> addSideEffectHandler(noinline handler: SideEffectEventHandler<T>) {
        handler as? SideEffectEventHandler<EVENT>
            ?: throw IllegalArgumentException("Event handler must have the right type")
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        registerSideEffectHandler(name, handler)
    }

    // No param handler (neither event nor emit needed)
    inline fun <reified T : EVENT> addNoParamHandler(noinline handler: NoParamEventHandler) {
        val name = T::class.simpleName
            ?: throw IllegalArgumentException("Anonymous objects not supported as type")
        registerNoParamHandler(name, handler)
    }

    fun registerHandler(name: String, handler: EventHandler<EVENT, STATE, EFFECT>) {
        checkHandlerConflict(name)
        fullEventHandlers[name] = handler
    }

    fun registerSimpleHandler(name: String, handler: SimpleEventHandler<STATE, EFFECT>) {
        checkHandlerConflict(name)
        simpleEventHandlers[name] = handler
    }

    fun registerSideEffectHandler(name: String, handler: SideEffectEventHandler<EVENT>) {
        checkHandlerConflict(name)
        sideEffectHandlers[name] = handler
    }

    fun registerNoParamHandler(name: String, handler: NoParamEventHandler) {
        checkHandlerConflict(name)
        noParamHandlers[name] = handler
    }

    private fun checkHandlerConflict(name: String) {
        if (fullEventHandlers.containsKey(name) ||
            simpleEventHandlers.containsKey(name) ||
            sideEffectHandlers.containsKey(name) ||
            noParamHandlers.containsKey(name)
        ) {
            throw IllegalStateException("Only one handler per event can be registered for $name")
        }
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

    // Keep navigation channel for backward compatibility during transition
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

    // Helper function to emit any effect safely
    protected fun emitEffect(effect: EFFECT) {
        emitter.effect(effect)
    }

    // Deprecated - use effects instead
    @Deprecated("Use effects for navigation instead", ReplaceWith("emitter.effect(NavigationEffect.*)"))
    fun navigate(event: NavigationEvent) {
        viewModelScope.launch {
            _navigationEventChannel.send(event)
        }
    }

    inline fun <reified S : STATE> ViewModelInterface<*, S, *>.updateState(onUpdate: (S) -> S) {
        if (state is MutableStateFlow<S>) {
            (state as MutableStateFlow<S>).update { onUpdate(it) }
        }
    }
}