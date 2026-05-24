package com.github.hilican.goandbe.data.repositories

import com.github.hilican.goandbe.data.remote.api.IHotelApiService
import com.github.hilican.goandbe.data.remote.mapper.toDTO
import com.github.hilican.goandbe.data.remote.mapper.toDomain
import com.github.hilican.goandbe.domain.iRepositories.IHotelApiRepository
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.ReserveRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelApiRepository @Inject constructor(
    private val api: IHotelApiService
) : IHotelApiRepository {

    /* ---------- Hotels ---------- */
    override suspend fun getHotels(groupId: String): List<Hotel> {
        return api.getHotels(groupId).map { it.toDomain() }
    }

    /* ---------- Availability ---------- */
    override suspend fun getAvailability(
        groupId: String,
        start: String,
        end: String,
        hotelId: String?,
        city: String?
    ): List<Hotel> {
        return api.getAvailability(groupId, start, end, hotelId, city)
            .available_hotels
            .map { it.toDomain() }
    }

    /* ---------- Reserve & Cancel (within group) ---------- */
    override suspend fun reserveByGroupId(
        groupId: String,
        request: ReserveRequest
    ): Reservation {
        return api.reserveRoom(groupId, request.toDTO()).reservation.toDomain()
    }

    override suspend fun cancelByGroupId(
        groupId: String,
        request: ReserveRequest
    ): String {
        return api.cancelReservation(groupId, request.toDTO()).message
    }

    /* ---------- Reservations for a group ---------- */
    override suspend fun getGroupReservations(
        groupId: String,
        guestEmail: String?
    ): List<Reservation> {
        return api.getGroupReservations(groupId, guestEmail).reservations.map { it.toDomain() }
    }

    /* ---------- All reservations ---------- */
    override suspend fun getAllReservations(): Map<String, List<Reservation>> {
        return api.getAllReservations()
            .groups
            .mapValues { entry -> entry.value.map { it.toDomain() } }
    }

    /* ---------- Single-ID operations ---------- */
    override suspend fun getReservationById(resId: String): Reservation {
        return api.getReservationById(resId).toDomain()
    }

    override suspend fun deleteById(resId: String): Reservation {
        return api.deleteReservationById(resId).toDomain()
    }
}