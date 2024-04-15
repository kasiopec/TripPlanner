package com.project.tripplanner

import com.project.tripplanner.ErrorState.NoConnectionError
import com.project.tripplanner.ErrorState.UnknownError
import io.github.jan.supabase.exceptions.HttpRequestException

interface MviErrorHandler<STATE, EFFECT, ERROR> {

    suspend fun handleError(exception: ERROR, emit: Emitter<STATE, EFFECT>)
}

class MviDefaultErrorHandler<STATE, EFFECT>(val createGlobalErrorState: (errorState: ErrorState) -> STATE) :
    MviErrorHandler<STATE, EFFECT, Exception> {

    override suspend fun handleError(exception: Exception, emit: Emitter<STATE, EFFECT>) {
        val errorState = when (exception) {
            is HttpRequestException -> NoConnectionError()
            else -> UnknownError()
        }
        emit.state(createGlobalErrorState(errorState))
    }
}