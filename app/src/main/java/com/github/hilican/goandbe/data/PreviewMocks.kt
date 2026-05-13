package com.github.hilican.goandbe.data

val mockTrip = Trip(
    tripId = 1,
    userId = "user123",
    name = "Viaje a Japón",
    startDate = 1714775586000L,
    endDate = 1714948386000L,
    totalCost = 1500L
)

val mockActivities = listOf(
    ItineraryItem(1, 1, "Visita Templo", 1714775586000L, 50L),
    ItineraryItem(2, 1, "Cena Sushi", 1714861986000L, 30L)
)

val mockActivitie = ItineraryItem(1, 1, "Visita Templo", 1714775586000L, 50L)

val mockTripWithItinerary = TripWithItinerary(
    trip = mockTrip,
    activities = mockActivities,
)

val mockListTrips = listOf(
    mockTripWithItinerary
)