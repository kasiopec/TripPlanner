package com.project.tripplanner.features.tripform.domain

import com.project.tripplanner.cover.TripCoverImageStorage
import com.project.tripplanner.data.model.Trip
import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.repositories.TripRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val tripCoverImageStorage: TripCoverImageStorage
) {
    suspend operator fun invoke(existingTrip: Trip, updatedInput: TripInput) {
        val updatedTrip = existingTrip.copy(
            destination = updatedInput.destination,
            startDate = updatedInput.startDate,
            endDate = updatedInput.endDate,
            timezone = updatedInput.timezone,
            coverImageUri = updatedInput.coverImageUri,
            notes = updatedInput.notes
        )

        tripRepository.updateTrip(updatedTrip)

        val previousCoverPath = existingTrip.coverImageUri
        val newCoverPath = updatedInput.coverImageUri
        if (!previousCoverPath.isNullOrBlank() && previousCoverPath != newCoverPath) {
            tripCoverImageStorage.delete(previousCoverPath)
        }
    }
}
