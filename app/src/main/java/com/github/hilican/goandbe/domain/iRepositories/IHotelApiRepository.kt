package com.github.hilican.goandbe.domain.iRepositories

import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.ReserveRequest

interface IHotelApiRepository {

    /* ---------- Hotels & Availability ---------- */
    suspend fun getHotels(groupId: String): List<Hotel>
    suspend fun getAvailability(
        groupId: String,
        start: String,
        end: String,
        hotelId: String? = null,
        city: String? = null
    ): List<Hotel>

    /* ---------- Make & cancel reservation (by group) ---------- */
    suspend fun reserveByGroupId(groupId: String, request: ReserveRequest): Reservation
    suspend fun cancelByGroupId(groupId: String, request: ReserveRequest): String   // returns message

    /* ---------- Reservations queries ---------- */
    suspend fun getGroupReservations(
        groupId: String,
        guestEmail: String? = null
    ): List<Reservation>

    suspend fun getAllReservations(): Map<String, List<Reservation>>

    /* ---------- Operations by reservation-id ---------- */
    suspend fun getReservationById(resId: String): Reservation
    suspend fun deleteById(resId: String): Reservation
}