package com.project.tripplanner.utils.validators

import com.project.tripplanner.utils.validators.PasswordError.ERROR_DIGIT
import com.project.tripplanner.utils.validators.PasswordError.ERROR_LENGTH
import com.project.tripplanner.utils.validators.PasswordError.ERROR_NOT_SAME
import com.project.tripplanner.utils.validators.PasswordError.ERROR_SPECIAL
import com.project.tripplanner.utils.validators.PasswordError.ERROR_UPPER
import com.project.tripplanner.utils.validators.PasswordError.ERROR_WHITESPACE
import javax.inject.Inject


enum class PasswordError {
    ERROR_LENGTH, ERROR_WHITESPACE, ERROR_DIGIT, ERROR_UPPER, ERROR_SPECIAL, ERROR_NOT_SAME
}

class PasswordValidator @Inject constructor() {
    data class PasswordResult(val isValid: Boolean, val errors: List<PasswordError>)

    fun isValid(password: String?, secondPassword: String?): PasswordResult {
        if (password == null) {
            return PasswordResult(isValid = false, errors = PasswordError.values().asList())
        } else {
            val errors = mutableListOf<PasswordError>()
            errors.apply {
                if (!password.any { it.isUpperCase() }) {
                    add(ERROR_UPPER)
                }
                if (!password.any { it.isDigit() }) {
                    add(ERROR_DIGIT)
                }
                if (!password.any { !it.isLetterOrDigit() }) {
                    add(ERROR_SPECIAL)
                }
                if (password.any { it.isWhitespace() }) {
                    add(ERROR_WHITESPACE)
                }
                if (password.length < 8) {
                    add(ERROR_LENGTH)
                }
                if (password != secondPassword) {
                    add(ERROR_NOT_SAME)
                }
            }
            return PasswordResult(isValid = errors.isEmpty(), errors = errors)
        }
    }
}