package com.github.hilican.goandbe

import com.github.hilican.goandbe.data.remote.DTO.ReserveRequestDTO
import com.github.hilican.goandbe.domain.HotelMock
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.Room

val jsonReservationSuccess = """
    {
        "nights": 6,
        "message": "Reserva creada con éxito"
    }
""".trimIndent()


val jsonCancelSuccess = """
    {
        "message": "Reserva cancelada correctamente"
    }
""".trimIndent()


object HotelTestData {
    val request = ReserveRequestDTO(
        hotel_id = "hotel-01",
        room_id = "room-202",
        start_date = "2026-06-01",
        end_date = "2026-06-04",
        guest_name = "Alex Gomez",
        guest_email = "alex@example.com"
    )

    val jsonReserveRoomSuccess = """
            {
                "message": "Reserva confirmada con éxito",
                "nights": 3,
                "reservation": {
                    "id": "res-101",
                    "hotel_id": "hotel-01",
                    "room_id": "room-202",
                    "start_date": "2026-06-01",
                    "end_date": "2026-06-04",
                    "guest_name": "Alex Gomez",
                    "guest_email": "alex@example.com",
                    "hotel": {
                        "id": "hotel-01",
                        "name": "Hotel Paraíso"
                    },
                    "room": {
                        "id": "room-202",
                        "type": "Deluxe Suite"
                    }
                }
            }
        """.trimIndent()

    val jsonAvailabilitySuccess = """
        {
            "available_hotels": [
                {
                    "id": "hotel-01",
                    "name": "Hotel Paraíso",
                    "address": "Calle Marítima 123",
                    "rating": 5,
                    "image_url": "https://example.com/image.jpg",
                    "rooms": []
                }
            ]
        }
    """.trimIndent()
}