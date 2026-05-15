package com.github.hilican.goandbe.data.repositories

import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripDao
import com.github.hilican.goandbe.data.TripWithItinerary
import com.github.hilican.goandbe.domain.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripRepository @Inject constructor (private val tripDao: TripDao) : ITripRepository {
    // READ
    override fun getTripsForUser(userId: String): Flow<List<TripWithItinerary>> {
        return tripDao.getTripsWithItineraryForUser(userId)
    }

    override suspend fun getTripById(id: Int): Trip? {
        return tripDao.getTripById(id)
    }

    // CREATE
    override suspend fun addTrip(trip: Trip) : Long {
        return tripDao.insertTrip(trip)
    }

    // UPDATE
    override suspend fun editTrip(updatedTrip: Trip) : Int {
        return tripDao.updateTrip(updatedTrip)
    }

    // DELETE
    override suspend fun deleteTrip(trip: Trip) : Int {
        return tripDao.deleteTrip(trip)
    }

    // -- FOR ACTIVITIES --
    override suspend fun addActivity(item: ItineraryItem) : Long = tripDao.insertItineraryItem(item)

    override suspend fun deleteActivity(item: ItineraryItem) : Int = tripDao.deleteItineraryItem(item)

    override suspend fun updateActivity(item: ItineraryItem) : Int = tripDao.updateItineraryItem(item)
}