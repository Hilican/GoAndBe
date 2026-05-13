package com.github.hilican.goandbe.data.repositories

import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripDao
import com.github.hilican.goandbe.data.TripWithItinerary
import com.github.hilican.goandbe.domain.ITripRepository
import kotlinx.coroutines.flow.Flow

class TripRepository (private val tripDao: TripDao) : ITripRepository {
    // READ
    override fun getTripsForUser(userId: String): Flow<List<TripWithItinerary>> {
        return tripDao.getTripsWithItineraryForUser(userId)
    }

    override suspend fun getTripById(id: Int): Trip? {
        return tripDao.getTripById(id)
    }

    // CREATE
    override suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    // UPDATE
    override suspend fun editTrip(updatedTrip: Trip) {
        tripDao.updateTrip(updatedTrip)
    }

    // DELETE
    override suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    // -- FOR ACTIVITIES --
    override suspend fun addActivity(item: ItineraryItem) = tripDao.insertItineraryItem(item)

    override suspend fun deleteActivity(item: ItineraryItem) = tripDao.deleteItineraryItem(item)

    override suspend fun updateActivity(item: ItineraryItem) = tripDao.insertItineraryItem(item)
}