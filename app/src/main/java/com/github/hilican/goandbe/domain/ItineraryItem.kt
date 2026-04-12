package com.github.hilican.goandbe.domain

import java.util.Date

data class ItineraryItem(
    val id: Int,
    val description: String,
    val activityTime: Date,
    val costEstimate: Double,
)

val mockItineraryItem = ItineraryItem(
    id = 1,
    description = "Visita al Coliseo",
    activityTime = java.util.Date(), // Fecha actual para el preview
    costEstimate = 25.50 // Precio en Double
)