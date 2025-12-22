package com.project.tripplanner.repositories

import androidx.room.withTransaction
import com.project.tripplanner.data.local.db.ItineraryDao
import com.project.tripplanner.data.local.db.ItinerarySortUpdate
import com.project.tripplanner.data.local.db.TripPlannerDatabase
import com.project.tripplanner.data.mapper.toDomain
import com.project.tripplanner.data.mapper.toEntity
import com.project.tripplanner.data.model.ItineraryItem
import com.project.tripplanner.data.model.ItineraryItemInput
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ItineraryRepository {
    fun observeItinerary(tripId: Long): Flow<List<ItineraryItem>>
    fun observeItineraryForDate(tripId: Long, localDate: LocalDate): Flow<List<ItineraryItem>>
    fun observeItem(itemId: Long): Flow<ItineraryItem?>
    suspend fun addItem(input: ItineraryItemInput): Long
    suspend fun updateItem(item: ItineraryItem)
    suspend fun deleteItem(itemId: Long)
    suspend fun deleteItemsForTrip(tripId: Long)
    suspend fun reorderItems(tripId: Long, localDate: LocalDate, orderedIds: List<Long>)
}

class ItineraryRepositoryImpl @Inject constructor(
    private val database: TripPlannerDatabase,
    private val itineraryDao: ItineraryDao
) : ItineraryRepository {
    override fun observeItinerary(tripId: Long): Flow<List<ItineraryItem>> {
        return itineraryDao.observeItinerary(tripId).map { items -> items.map { it.toDomain() } }
    }

    override fun observeItineraryForDate(tripId: Long, localDate: LocalDate): Flow<List<ItineraryItem>> {
        return itineraryDao.observeItineraryForDate(tripId, localDate).map { items -> items.map { it.toDomain() } }
    }

    override fun observeItem(itemId: Long): Flow<ItineraryItem?> {
        return itineraryDao.observeItem(itemId).map { it?.toDomain() }
    }

    override suspend fun addItem(input: ItineraryItemInput): Long {
        return database.withTransaction {
            val sortOrder = resolveSortOrder(input)
            itineraryDao.insertItem(input.toEntity(sortOrder))
        }
    }

    override suspend fun updateItem(item: ItineraryItem) {
        itineraryDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(itemId: Long) {
        itineraryDao.deleteItem(itemId)
    }

    override suspend fun deleteItemsForTrip(tripId: Long) {
        itineraryDao.deleteItemsForTrip(tripId)
    }

    override suspend fun reorderItems(tripId: Long, localDate: LocalDate, orderedIds: List<Long>) {
        if (orderedIds.isEmpty()) return
        database.withTransaction {
            val existingIds = itineraryDao.getItemIdsForDate(tripId, localDate).toSet()
            val updates = orderedIds.filter { it in existingIds }
                .mapIndexed { index, id -> ItinerarySortUpdate(id = id, sortOrder = index.toLong()) }
            if (updates.isNotEmpty()) {
                itineraryDao.updateSortOrders(updates)
            }
        }
    }

    private suspend fun resolveSortOrder(input: ItineraryItemInput): Long {
        val maxSortOrder = itineraryDao.getMaxSortOrder(input.tripId)
        val timeBased = input.localTime?.toSecondOfDay()?.toLong()
        return input.sortOrder ?: timeBased ?: maxSortOrder + 1
    }
}
