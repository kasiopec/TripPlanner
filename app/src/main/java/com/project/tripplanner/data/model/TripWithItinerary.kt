package com.project.tripplanner.data.model

data class TripWithItinerary(
    val trip: Trip,
    val itinerary: List<ItineraryItem>
)
