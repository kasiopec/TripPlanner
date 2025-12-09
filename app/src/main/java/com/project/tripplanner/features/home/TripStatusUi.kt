package com.project.tripplanner.features.home

import androidx.annotation.StringRes
import com.project.tripplanner.R

enum class TripStatusUi(@StringRes val labelResId: Int) {
    None(R.string.home_status_upcoming),
    InProgress(R.string.trip_status_in_progress),
    Ended(R.string.trip_status_ended)
}

