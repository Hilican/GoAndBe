package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.R
import com.github.hilican.goandbe.data.Room.HotelRoom
import com.github.hilican.goandbe.data.Room.ItineraryItem
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.data.Room.RoomRoom
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.data.Room.TripWithItinerary
import com.github.hilican.goandbe.data.Room.UserRoom
import com.github.hilican.goandbe.domain.model.Address
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.Room
import com.github.hilican.goandbe.viewmodel.HotelUiState

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

    val mockTripWithDetails = TripWithDetails(
        trip = mockTrip,
        activities = mockActivities,
        // Reutilizamos la lista de reservas que ya tienes definida en HotelMock
        reservations = HotelMock.mockListReservation
    )

    val mockListTripsWithDetails = listOf(
        mockTripWithDetails
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

    val mockUserRoom = UserRoom(
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

    val newEmptyUserRoom = UserRoom(
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

object HotelMock {
    // 🌟 Usamos lazy para que no se intenten generar estas URIs de recursos hasta que se usen de verdad
    val image by lazy { "android.resource://com.github.hilican.goandbe.domain/" + R.drawable.ic_launcher_foreground }
    val image2 by lazy { "android.resource://com.github.hilican.goandbe.domain/" + R.drawable.ic_launcher_background }
    val mockImages by lazy { listOf(image2, image) }

    val mockHotelRoom = HotelRoom(
        id = "1",
        name = "Grand Hotel Luxury & Spa",
    )

    val mockRoomRoom by lazy {
        RoomRoom(
            id = "101",
            roomType = "suite presidencial",
            price = 150.0f,
            images = listOf(image)
        )
    }

    val mockReservationRoom by lazy {
        ReservationRoom(
            id = "res-999",
            hotelId = "1",
            roomId = "101",
            startDate = "24/05/2026",
            endDate = "31/05/2026",
            guestName = "John Doe",
            guestEmail = "john.doe@example.com",
            hotel = mockHotelRoom,
            room = mockRoomRoom,
            parentTripId = 1
        )
    }

    val mockReservationRoom1 by lazy {
        ReservationRoom(
            id = "res-001",
            hotelId = "5",
            roomId = "134",
            startDate = "23/03/2026",
            endDate = "5/04/2026",
            guestName = "Doue",
            guestEmail = "dou.doe@example.com",
            hotel = mockHotelRoom,
            room = mockRoomRoom,
            parentTripId = 1
        )
    }

    val mockListReservation by lazy { listOf(mockReservationRoom, mockReservationRoom1) }

    val mockRoom by lazy {
        Room(
            id = "101",
            roomType = "suite presidencial",
            price = 150.0f,
            images = listOf(image)
        )
    }

    val mockRoom1 by lazy {
        Room(
            id = "102",
            roomType = "double bed",
            price = 150.0f,
            images = listOf(image)
        )
    }

    val mockHotel by lazy {
        Hotel(
            id = "hotel_abc",
            name = "Grand Luxury Resort",
            address = "Calle de la Playa 123",
            rating = 4,
            imageUrl = image,
            rooms = listOf(mockRoom)
        )
    }

    val mockHotel2 by lazy {
        Hotel(
            id = "hotel_xyz",
            name = "Grand Luxury Resort",
            address = "Calle de la Playa 123",
            rating = 4,
            imageUrl = image,
            rooms = listOf(mockRoom1)
        )
    }

    val mockReservation by lazy {
        Reservation(
            id = "res_999",
            hotelId = mockHotel.id,
            roomId = mockRoom.id,
            startDate = "2026-06-01",
            endDate = "2026-06-10",
            guestName = "Pepe",
            guestEmail = "pepe@correo.com",
            hotel = mockHotel,
            room = mockRoom
        )
    }

    val mockHotelUi = HotelUiState(
        isLoading = false,
        errorMessage = null,
        hotels = listOf(mockHotel, mockHotel2),
        availableHotels = listOf(mockHotel, mockHotel2)
    )

    val mockHotelUiWithHotelId = HotelUiState(
        isLoading = false,
        errorMessage = null,
        hotels = listOf(mockHotel, mockHotel2),
        availableHotels = listOf(mockHotel, mockHotel2),
        hotelId = mockHotel.id
    )
}