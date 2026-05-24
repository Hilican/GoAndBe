package com.github.hilican.goandbe.data.remote.mapper

import com.github.hilican.goandbe.data.remote.DTO.HotelDTO
import com.github.hilican.goandbe.data.remote.DTO.ReservationDTO
import com.github.hilican.goandbe.data.remote.DTO.ReserveRequestDTO
import com.github.hilican.goandbe.data.remote.DTO.RoomDTO
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.ReserveRequest
import com.github.hilican.goandbe.domain.model.Room

fun HotelDTO.toDomain(): Hotel = Hotel(
    id        = id,
    name      = name,
    address   = address,
    rating    = rating,
    imageUrl  = image_url,
    rooms     = rooms
        ?.map { it.toDomain() }      // si no es null lo mapea
        ?: emptyList()               // si es null lista vacía
)

fun RoomDTO.toDomain(): Room = Room(
    id       = id,
    roomType = room_type,
    price    = price,
    images   = images ?: emptyList()
)

fun ReservationDTO.toDomain(): Reservation = Reservation(
    id         = id,
    hotelId    = hotel_id,
    roomId     = room_id,
    startDate  = start_date,
    endDate    = end_date,
    guestName  = guest_name,
    guestEmail = guest_email,
    hotel = hotel?.toDomain() ?: Hotel(
            id = hotel_id ?: "",
            name = "Confirmado",
            address = "",
            rating = 0,
            imageUrl = "",
            rooms = emptyList()
        ),
    room = room?.toDomain() ?: Room(
        id = room_id ?: "",
        roomType = "Confirmada",
        price = 0.0f,
        images = emptyList()
    )
)

fun ReserveRequest.toDTO(): ReserveRequestDTO = ReserveRequestDTO(
    hotel_id = hotelId,
    room_id = roomId,
    start_date = startDate,
    end_date = endDate,
    guest_name = guestName,
    guest_email = guestEmail
)