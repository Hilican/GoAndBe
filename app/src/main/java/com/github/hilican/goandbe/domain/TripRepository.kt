package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripDao
import com.github.hilican.goandbe.data.TripWithItinerary
import kotlinx.coroutines.flow.Flow


class TripRepository (private val tripDao: TripDao) {
    // READ
    fun getTripsForUser(userId: String): Flow<List<TripWithItinerary>> {
        return tripDao.getTripsWithItineraryForUser(userId)
    }

    suspend fun getTripById(id: Int): Trip? {
        return tripDao.getTripById(id)
    }

    // CREATE
    suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    // UPDATE
    suspend fun editTrip(updatedTrip: Trip) {
        tripDao.updateTrip(updatedTrip)
    }

    // DELETE
    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    // Para las actividades (ItineraryItems)
    suspend fun addActivity(item: ItineraryItem) = tripDao.insertItineraryItem(item)

    suspend fun updateActivity(item: ItineraryItem) = tripDao.insertItineraryItem(item)

    suspend fun deleteActivity(item: ItineraryItem) = tripDao.deleteItineraryItem(item)
}