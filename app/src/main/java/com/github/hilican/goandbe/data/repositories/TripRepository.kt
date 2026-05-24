package com.github.hilican.goandbe.data.repositories

import android.util.Log
import com.github.hilican.goandbe.data.Room.ItineraryItem
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.data.Room.ReservationWithTrip
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripDao
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.data.Room.TripWithItinerary
import com.github.hilican.goandbe.domain.iRepositories.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripRepository @Inject constructor (
    private val tripDao: TripDao
) : ITripRepository {
    // READ

    //OLD ONE
    override fun getTripsWithItineraryForUser(userId: String): Flow<List<TripWithItinerary>> {
        return tripDao.getTripsWithItineraryForUser(userId)
    }

    override fun getTripsForUser(userId: String): Flow<List<TripWithDetails>> {
        return tripDao.getTripsWithDetailsForUser(userId)
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

    // -- FOR RESERVES --
    override fun getUserReserves(userId: String): Flow<List<ReservationRoom>> = tripDao.getUserReservations(userId)

    override suspend fun getReservationById(id: String): Result<ReservationRoom> {
        return try {
            val reservation = tripDao.getReserveById(id)

            if (reservation != null) {
                Result.success(reservation)
            } else {
                Result.failure(NoSuchElementException("No se encontró ninguna reserva con el ID: $id"))
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Error crítico al consultar Room por ID: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun addReserve(item: ReservationRoom): Result<Long> {
        return try {
            val rowId = tripDao.insertReservation(item)

            if (rowId > 0) {
                Result.success(rowId)
            } else {
                Result.failure(Exception("Error al insertar la fila en la base de datos local"))
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Error crítico al añadir en Room: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
    override suspend fun deleteReserve(item: ReservationRoom): Result<Int> {
        return try {
            val rowsAffected = tripDao.deleteReservation(item)

            if (rowsAffected > 0) {
                Result.success(rowsAffected)
            } else {
                // Devolvemos un fallo controlado si no se borró ninguna fila
                Result.failure(Exception("No se encontró la reserva para eliminar en la base de datos local"))
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Error crítico al eliminar en Room: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
    override suspend fun updateReserve(item: ReservationRoom): Result<Int> {
        return try {
            val rowsAffected = tripDao.updateReservation(item)

            if (rowsAffected > 0) {
                Result.success(rowsAffected)
            } else {
                // Devolvemos un fallo controlado si no se actualizó ninguna fila
                Result.failure(Exception("No se encontró la reserva para actualizar en la base de datos local"))
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Error crítico al actualizar en Room: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}