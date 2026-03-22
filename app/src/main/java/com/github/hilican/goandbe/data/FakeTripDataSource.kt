package com.github.hilican.goandbe.data

import com.github.hilican.goandbe.domain.*
import java.util.Date


object FakeTripDataSource {
    private val trips = mutableListOf<Trip>(
        Trip(
            id = 1,
            name = "Viaje a París",
            startDate = "15/05/2026",
            endDate = "22/05/2026",
            totalCost = 65L, // Nota la 'L' para indicar que es Long
            createdAt = System.currentTimeMillis(),
            activities = listOf(
                ItineraryItem(
                    id = 0,
                    description = "Visita al Museo del Louvre",
                    activityTime = Date(System.currentTimeMillis() + 86400000), // +1 día
                    costEstimate = 20.0 // Decimal para indicar Double
                ),
                ItineraryItem(
                    id = 1,
                    description = "Cena cerca de la Torre Eiffel",
                    activityTime = Date(System.currentTimeMillis() + 172800000), // +2 días
                    costEstimate = 45.0
                )
            )
        ),
        Trip(
            id = 2,
            name = "Escapada a Roma",
            startDate = "10/08/2026",
            endDate = "14/08/2026",
            totalCost = 0L,
            createdAt = System.currentTimeMillis() - 10000, // Un poco más antiguo
            activities = emptyList()
        )
    )

    fun getTrips(): List<Trip>
    {
        return trips
    }
}