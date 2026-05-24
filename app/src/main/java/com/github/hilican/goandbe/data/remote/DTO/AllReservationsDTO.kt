package com.github.hilican.goandbe.data.remote.DTO

data class AllReservationsDTO(
    val groups: Map<String, List<ReservationDTO>>
)