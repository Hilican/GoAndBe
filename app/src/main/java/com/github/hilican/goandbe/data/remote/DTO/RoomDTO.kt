package com.github.hilican.goandbe.data.remote.DTO

data class RoomDTO(
    val id: String,
    val room_type: String,
    val price: Float,
    val images: List<String>
)