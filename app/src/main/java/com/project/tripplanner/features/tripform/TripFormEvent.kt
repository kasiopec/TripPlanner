package com.project.tripplanner.features.tripform

import android.net.Uri
import com.project.tripplanner.Event

sealed class TripFormEvent : Event {
    data class ScreenLoaded(val tripId: Long?) : TripFormEvent()
    data class DestinationChanged(val value: String) : TripFormEvent()
    data object StartDateClicked : TripFormEvent()
    data object EndDateClicked : TripFormEvent()
    data class StartDateSelected(val millis: Long) : TripFormEvent()
    data class EndDateSelected(val millis: Long) : TripFormEvent()
    data object DatePickerDismissed : TripFormEvent()
    data class SingleDayToggled(val enabled: Boolean) : TripFormEvent()
    data class NotesChanged(val value: String) : TripFormEvent()
    data class CoverImageSelected(val uri: Uri) : TripFormEvent()
    data object SaveClicked : TripFormEvent()
    data object BackClicked : TripFormEvent()
}


