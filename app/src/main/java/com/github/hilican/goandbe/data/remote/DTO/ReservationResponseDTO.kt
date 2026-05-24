package com.github.hilican.goandbe.data.remote.DTO

data class ReservationResponseDTO (
    val message: String,
    val nights: Int,
    val reservation: ReservationDTO        // ya trae hotel + room
)