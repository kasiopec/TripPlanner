package com.project.tripplanner.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TripWithItineraryEntity(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId",
        entity = ItineraryItemEntity::class
    )
    val itinerary: List<ItineraryItemEntity>
)
