package com.project.tripplanner

import androidx.annotation.StringRes

sealed class ErrorState : State {
    @get:StringRes
    abstract val titleId: Int

    @get:StringRes
    abstract val message: Int

    class NoConnectionError() : ErrorState() {
        override val titleId: Int = R.string.error_no_connection_title
        override val message: Int = R.string.error_no_connection_message
    }

    class UnknownError() : ErrorState() {
        override val titleId: Int = R.string.error_unknown_title
        override val message: Int = R.string.error_unknown_message
    }

    class SessionExpiredError() : ErrorState() {
        override val titleId: Int = R.string.error_session_expired_title
        override val message: Int = R.string.error_session_expired_message
    }
}