package com.project.tripplanner.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.tripplanner.data.local.entity.ItineraryItemEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId ORDER BY sortOrder, id")
    fun observeItinerary(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId AND localDate = :localDate ORDER BY sortOrder, id")
    fun observeItineraryForDate(tripId: Long, localDate: LocalDate): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE id = :itemId LIMIT 1")
    fun observeItem(itemId: Long): Flow<ItineraryItemEntity?>

    @Insert
    suspend fun insertItem(item: ItineraryItemEntity): Long

    @Update
    suspend fun updateItem(item: ItineraryItemEntity)

    @Update(entity = ItineraryItemEntity::class)
    suspend fun updateSortOrders(updates: List<ItinerarySortUpdate>)

    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)

    @Query("DELETE FROM itinerary_items WHERE tripId = :tripId")
    suspend fun deleteItemsForTrip(tripId: Long)

    @Query("SELECT id FROM itinerary_items WHERE tripId = :tripId AND localDate = :localDate ORDER BY sortOrder, id")
    suspend fun getItemIdsForDate(tripId: Long, localDate: LocalDate): List<Long>

    @Query("SELECT id FROM itinerary_items WHERE tripId = :tripId ORDER BY sortOrder, id")
    suspend fun getItemIdsForTrip(tripId: Long): List<Long>

    @Query("SELECT COALESCE(MAX(sortOrder), 0) FROM itinerary_items WHERE tripId = :tripId")
    suspend fun getMaxSortOrder(tripId: Long): Long
}

data class ItinerarySortUpdate(
    val id: Long,
    val sortOrder: Long
)
