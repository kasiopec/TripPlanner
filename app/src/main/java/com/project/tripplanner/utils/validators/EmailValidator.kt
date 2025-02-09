package com.project.tripplanner.utils.validators

import javax.inject.Inject

class EmailValidator @Inject constructor() {
    private object Patterns {
        val emailPattern =
            Regex("^[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+$")
    }

    fun isValid(entry: String?): Boolean {
        return entry != null && Patterns.emailPattern.matches(entry)
    }
}