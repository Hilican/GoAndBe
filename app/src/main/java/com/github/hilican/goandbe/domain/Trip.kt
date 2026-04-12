package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.domain.ItineraryItem

data class Trip(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val createdAt: Long = System.currentTimeMillis(), // Used for sorting
    val totalCost: Long = 0,
    val activities: List<ItineraryItem> = emptyList()
)

val mockTrip = Trip(
    id = 1,
    name = "Viaje a Roma",
    startDate = "20/05/2026",
    endDate = "24/05/2026",
    createdAt = System.currentTimeMillis(), // Fecha de creación: ahora mismo
    totalCost = 150L, // Coste total (Long)
    activities = listOf(
        ItineraryItem(
            id = 1,
            description = "Visita al Coliseo",
            activityTime = java.util.Date(), // Fecha actual para el preview
            costEstimate = 25.50 // Precio en Double
        ),
        ItineraryItem(
            id = 2,
            description = "Cena en Trastevere",
            activityTime = java.util.Date(),
            costEstimate = 40.0
        )
    )
)
