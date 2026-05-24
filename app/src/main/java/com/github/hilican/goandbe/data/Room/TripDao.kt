package com.github.hilican.goandbe.data.Room

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

    // --- Operaciones de Reserva ---
    @Insert
    suspend fun insertReservation(item: ReservationRoom) : Long
    @Update
    suspend fun updateReservation(item: ReservationRoom) : Int
    @Delete
    suspend fun deleteReservation(item: ReservationRoom) : Int
    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReserveById(id: String): ReservationRoom?



    @Transaction
    @Query("SELECT * FROM trips WHERE userId = :currentUserId")
    fun getTripsWithItineraryForUser(currentUserId: String): Flow<List<TripWithItinerary>>

    @Transaction
    @Query("SELECT * FROM trips WHERE userId = :currentUserId")
    fun getTripsWithReservationForUser(currentUserId: String): Flow<List<TripWithReservation>>

    @Transaction
    @Query("SELECT * FROM trips WHERE userId = :currentUserId")
    fun getTripsWithDetailsForUser(currentUserId: String): Flow<List<TripWithDetails>>

    @Query("""
        SELECT reservations.* FROM reservations 
        INNER JOIN trips ON reservations.parentTripId = trips.tripId 
        WHERE trips.userId = :currentUserId
    """)
    fun getUserReservations(currentUserId: String): Flow<List<ReservationRoom>>

    @Transaction
    @Query("""
        SELECT reservations.* FROM reservations 
        INNER JOIN trips ON reservations.parentTripId = trips.tripId 
        WHERE trips.userId = :currentUserId
    """)
    fun getUserReservationsWithTrip(currentUserId: String): Flow<List<ReservationWithTrip>>
}