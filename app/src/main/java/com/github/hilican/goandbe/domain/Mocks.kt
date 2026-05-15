package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripWithItinerary
import com.github.hilican.goandbe.data.User

object TripMocks{
    val mockTrip = Trip(
        tripId = 1,
        userId = "user123",
        name = "Viaje a Japón",
        startDate = 1714775586000L,
        endDate = 1714948386000L,
        totalCost = 1500L
    )

    val mockActivities = listOf(
        ItineraryItem(
            1,
            1,
            "Visita Templo",
            1714775586000L,
            50L
        ),
        ItineraryItem(
            2,
            1,
            "Cena Sushi",
            1714861986000L,
            30L
        )
    )

    val mockActivitie = ItineraryItem(
        1,
        1,
        "Visita Templo",
        1714775586000L,
        50L
    )

    val mockTripWithItinerary = TripWithItinerary(
        trip = mockTrip,
        activities = mockActivities,
    )

    val mockListTrips = listOf(
        mockTripWithItinerary
    )
}

object UserMock{
    val mockAddress = Address(
        street = "Calle Mayor 15, 3°B",
        city = "Madrid",
        state = "Madrid",
        zipCode = "28013",
        country = "España",
        additionalInfo = "Llamar al timbre 2"
    )

    val mockUser = User(
        userId = "uuid_firebase_123456789",
        email = "juan.perez@ejemplo.com",
        username = "juanito99",
        dateOfBirth = 946684800000L, // 1 de enero de 2000 en milisegundos
        createdAt = System.currentTimeMillis(),
        address = mockAddress,
        phoneNumber = "+34 600 123 456",
        receiveEmails = true
    )

    // ---------------------------------------------------------
    // EXTRAS RECOMENDADOS PARA PREVIEWS
    // ---------------------------------------------------------

    val emptyAddress = Address(
        street = "",
        city = "",
        state = "",
        zipCode = "",
        country = "",
        additionalInfo = null
    )

    val newEmptyUser = User(
        userId = "uuid_firebase_nuevo_000",
        email = "nuevo@ejemplo.com",
        username = "usuario_nuevo",
        dateOfBirth = 0L,
        createdAt = System.currentTimeMillis(),
        address = emptyAddress,
        phoneNumber = "",
        receiveEmails = false
    )
}
