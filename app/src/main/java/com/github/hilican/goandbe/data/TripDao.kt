package com.github.hilican.goandbe.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    // --- Operaciones de Viajes ---
    @Insert
    suspend fun insertTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)


    @Query("SELECT * FROM trips WHERE tripId = :id")
    suspend fun getTripById(id: Int): Trip?

    // --- Operaciones de Itinerario ---

    @Insert
    suspend fun insertItineraryItem(item: ItineraryItem)
    @Update
    suspend fun updateItineraryItem(item: ItineraryItem)
    @Delete
    suspend fun deleteItineraryItem(item: ItineraryItem)

    @Transaction // Necesario cuando usamos @Relation
    @Query("SELECT * FROM trips WHERE userId = :currentUserId")
    fun getTripsWithItineraryForUser(currentUserId: String): Flow<List<TripWithItinerary>>
}