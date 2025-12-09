package com.project.tripplanner.features.home

import androidx.annotation.StringRes
import com.project.tripplanner.R

enum class HomeFilter(@StringRes val labelResId: Int) {
    All(R.string.home_filter_all),
    Upcoming(R.string.home_filter_upcoming),
    Ended(R.string.home_filter_ended)
}

