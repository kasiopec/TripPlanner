package com.project.tripplanner.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AuthManager(
    val supabaseAuth: Auth
) {
    private val refreshMutex = Mutex()

    suspend fun getAccessToken(): String {
        supabaseAuth.currentAccessTokenOrNull()?.let { return it }
        refreshToken()
        return supabaseAuth.currentAccessTokenOrNull() ?: throw IllegalStateException("No session")
    }

    suspend fun <T> withAuthRetry(block: suspend (String) -> T): T {
        val token = getAccessToken()
        return try {
            block(token)
        } catch (e: Throwable) {
            if (isUnauthorized(e)) {
                refreshToken()
                val newToken = getAccessToken()
                block(newToken)
            } else throw e
        }
    }

    suspend fun refreshToken() = refreshMutex.withLock {
        try {
            supabaseAuth.refreshCurrentSession()
        } catch (_: Exception) {
            // caller will handle, silently swallow
        }
    }

    private fun isUnauthorized(e: Throwable) = e is UnauthorizedRestException || e is AuthRestException
}
