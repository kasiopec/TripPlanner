package com.project.tripplanner.features.tripform.domain

import com.project.tripplanner.data.model.TripInput
import com.project.tripplanner.repositories.TripRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(input: TripInput): Long {
        return tripRepository.createTrip(input)
    }
}
