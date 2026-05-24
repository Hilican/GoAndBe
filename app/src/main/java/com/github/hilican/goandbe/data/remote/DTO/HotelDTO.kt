package com.github.hilican.goandbe.data.remote.DTO

data class HotelDTO (
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val image_url: String,
    val rooms: List<RoomDTO>? = null
)