package com.github.hilican.goandbe.domain.iRepositories

import com.github.hilican.goandbe.data.Room.ItineraryItem
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.data.Room.ReservationWithTrip
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.data.Room.TripWithItinerary
import kotlinx.coroutines.flow.Flow

interface ITripRepository {
    // READ
    fun getTripsForUser(userId: String): Flow<List<TripWithDetails>>

    suspend fun getTripById(id: Int): Trip?

    // CREATE
    suspend fun addTrip(trip: Trip): Long

    // UPDATE
    suspend fun editTrip(updatedTrip: Trip): Int

    // DELETE
    suspend fun deleteTrip(trip: Trip): Int

    // -- FOR ACTIVITIES --
    suspend fun addActivity(item: ItineraryItem): Long

    suspend fun deleteActivity(item: ItineraryItem): Int

    suspend fun updateActivity(item: ItineraryItem): Int

    // -- FOR RESERVES --
    fun getUserReserves(userId: String): Flow<List<ReservationRoom>>
    suspend fun addReserve(item: ReservationRoom): Result<Long>
    suspend fun deleteReserve(item: ReservationRoom): Result<Int>
    suspend fun updateReserve(item: ReservationRoom): Result<Int>
    suspend fun getReservationById(id: String): Result<ReservationRoom>
    fun getTripsWithItineraryForUser(userId: String): Flow<List<TripWithItinerary>>
}