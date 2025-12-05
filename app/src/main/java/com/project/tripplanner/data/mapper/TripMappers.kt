package com.project.tripplanner.data.mapper

import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import com.project.tripplanner.data.local.entity.TripEntity
import com.project.tripplanner.data.local.entity.TripWithItineraryEntity
import com.project.tripplanner.data.model.ItineraryItem
import com.project.tripplanner.data.model.ItineraryItemInput
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.data.model.TripWithItinerary
import java.time.Instant
import java.time.ZoneId

fun TripEntity.toDomain(): Trip {
    return Trip(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        timezone = ZoneId.of(timezone),
        coverImageUri = coverImageUri,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Trip.toEntity(): TripEntity {
    return TripEntity(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        timezone = timezone.id,
        coverImageUri = coverImageUri,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TripInput.toEntity(timestamp: Instant): TripEntity {
    return TripEntity(
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        timezone = timezone.id,
        coverImageUri = coverImageUri,
        notes = notes,
        createdAt = timestamp,
        updatedAt = timestamp
    )
}

fun ItineraryItemEntity.toDomain(): ItineraryItem {
    return ItineraryItem(
        id = id,
        tripId = tripId,
        localDate = localDate,
        localTime = localTime,
        title = title,
        type = type,
        location = location,
        notes = notes,
        sortOrder = sortOrder
    )
}

fun ItineraryItem.toEntity(): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        tripId = tripId,
        localDate = localDate,
        localTime = localTime,
        title = title,
        type = type,
        location = location,
        notes = notes,
        sortOrder = sortOrder
    )
}

fun ItineraryItemInput.toEntity(sortOrder: Long): ItineraryItemEntity {
    return ItineraryItemEntity(
        tripId = tripId,
        localDate = localDate,
        localTime = localTime,
        title = title,
        type = type,
        location = location,
        notes = notes,
        sortOrder = sortOrder
    )
}

fun TripWithItineraryEntity.toDomain(): TripWithItinerary {
    return TripWithItinerary(
        trip = trip.toDomain(),
        itinerary = itinerary.map { it.toDomain() }
    )
}
