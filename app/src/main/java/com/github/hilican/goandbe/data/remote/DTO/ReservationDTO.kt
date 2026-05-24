package com.github.hilican.goandbe.data.remote.DTO
data class ReservationDTO (
    val id: String,
    val hotel_id: String,
    val room_id: String,
    val start_date: String,
    val end_date: String,
    val guest_name: String,
    val guest_email: String,

    /* nuevos campos (pueden ser null si backend los omite en otro endpoint) */
    val hotel: HotelDTO?,
    val room: RoomDTO?
)