package com.github.hilican.goandbe.data.remote.api

import com.github.hilican.goandbe.data.remote.DTO.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.DELETE

interface IHotelApiService {

    /* ------------ Hotels & Availability ------------ */

    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String
    ): List<HotelDTO>

    @GET("hotels/{group_id}/availability")
    suspend fun getAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date")   endDate: String,
        @Query("hotel_id")   hotelId: String? = null,
        @Query("city")   city: String? = null
    ): AvailabilityResponseDTO

    /* ------------ Reservations by group ------------ */
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequestDTO
    ): ReservationResponseDTO

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body             request: CancelRequestDTO           // same fields as Reserve
    ): ApiMessageDTO                                           // e.g. { "message": "Reserva cancelada" }

    @GET("hotels/{group_id}/reservations")
    suspend fun getGroupReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): ReservationsWrapperDTO                                  // { reservations:[...] }

    /* ------------ Admin-level (all groups) ------------ */

    @GET("reservations")
    suspend fun getAllReservations(): AllReservationsDTO       // { groups:{ G01:[...], G02:[...] } }

    @GET("reservations/{res_id}")
    suspend fun getReservationById(
        @Path("res_id") resId: String
    ): ReservationDTO

    @DELETE("reservations/{res_id}")
    suspend fun deleteReservationById(
        @Path("res_id") resId: String
    ): ReservationDTO                                          // returns the deleted object
}