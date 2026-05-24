package com.github.hilican.goandbe.data.remote.DTO

data class ReserveRequestDTO (
    val hotel_id: String,
    val room_id: String,
    val start_date: String,
    val end_date: String,
    val guest_name: String,
    val guest_email: String
)