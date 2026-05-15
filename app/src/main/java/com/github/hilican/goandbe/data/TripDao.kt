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
    suspend fun insertTrip(trip: Trip) : Long // <-- Devuelve positivo si salio bien, -1 si ha habido error

    @Update
    suspend fun updateTrip(trip: Trip) : Int // <-- Devuelve numero de filas modificadas

    @Delete
    suspend fun deleteTrip(trip: Trip) : Int // <-- Devuelve numero de filas modificadas


    @Query("SELECT * FROM trips WHERE tripId = :id")
    suspend fun getTripById(id: Int): Trip?

    // --- Operaciones de Itinerario ---

    @Insert
    suspend fun insertItineraryItem(item: ItineraryItem) : Long
    @Update
    suspend fun updateItineraryItem(item: ItineraryItem) : Int
    @Delete
    suspend fun deleteItineraryItem(item: ItineraryItem) : Int

    @Transaction // Necesario cuando usamos @Relation
    @Query("SELECT * FROM trips WHERE userId = :currentUserId")
    fun getTripsWithItineraryForUser(currentUserId: String): Flow<List<TripWithItinerary>>
}