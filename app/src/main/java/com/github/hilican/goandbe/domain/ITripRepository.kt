package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripWithItinerary
import kotlinx.coroutines.flow.Flow

interface ITripRepository {
    // READ
    fun getTripsForUser(userId: String): Flow<List<TripWithItinerary>>

    suspend fun getTripById(id: Int): Trip?

    // CREATE
    suspend fun addTrip(trip: Trip)

    // UPDATE
    suspend fun editTrip(updatedTrip: Trip)

    // DELETE
    suspend fun deleteTrip(trip: Trip)

    // -- FOR ACTIVITIES --
    suspend fun addActivity(item: ItineraryItem)

    suspend fun deleteActivity(item: ItineraryItem)

    suspend fun updateActivity(item: ItineraryItem)

}