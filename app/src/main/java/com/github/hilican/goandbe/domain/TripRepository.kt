package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.FakeTripDataSource


class TripRepository {
    // In-memory list to hold the trips
    //private val trips = mutableListOf<Trip>()
    private val trips = FakeTripDataSource.getTrips().toMutableList()

    // READ
    fun getAllTrips(): List<Trip> {
        return trips.toList() // Return a copy to prevent external modification
    }

    fun getTripById(id: Int): Trip? {
        return trips.find { it.id == id }
    }

    fun getAllTripsLenght(): Int {
        return trips.size
    }

    // CREATE
    fun addTrip(trip: Trip) {
        trips.add(trip)
    }

    // UPDATE
    fun editTrip(updatedTrip: Trip) {
        val index = trips.indexOfFirst { it.id == updatedTrip.id }
        if (index != -1) {
            trips[index] = updatedTrip
        }
    }

    // DELETE
    fun deleteTrip(tripId: Int) {
        trips.removeAll { it.id == tripId }
    }

    private var currentActivityIdCounter = 0

    fun getNewActivityId(): Int {
        val id = currentActivityIdCounter
        currentActivityIdCounter++
        return id
    }
}