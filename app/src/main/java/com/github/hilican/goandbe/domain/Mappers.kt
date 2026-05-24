package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.Room.HotelRoom
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.data.Room.RoomRoom
import com.github.hilican.goandbe.domain.model.Reservation
import com.github.hilican.goandbe.domain.model.ReserveRequest

fun Reservation.toRoomEntity(parentTripId: Int): ReservationRoom {
    return ReservationRoom(
        id = this.id,
        parentTripId = parentTripId,
        hotelId = this.hotelId,
        roomId = this.roomId,
        startDate = this.startDate,
        endDate = this.endDate,
        guestName = this.guestName,
        guestEmail = this.guestEmail,

        // Mapeamos el objeto Hotel a HotelRoom
        hotel = HotelRoom(
            id = this.hotel.id,
            name = this.hotel.name
        ),

        // Mapeamos el objeto Room a RoomRoom
        room = RoomRoom(
            id = this.room.id,
            roomType = this.room.roomType,
            price = this.room.price,
            images = this.room.images
        ),
    )
}

fun Reservation.toRequest(name: String = this.guestName, email : String = this.guestEmail): ReserveRequest {
    return ReserveRequest(
        hotelId = this.hotelId,
        roomId = this.roomId,
        startDate = this.startDate,
        endDate = this.endDate,
        guestName = name,
        guestEmail = email,
    )
}

fun ReservationRoom.toRequest(): ReserveRequest {
    return ReserveRequest(
        hotelId = this.hotelId,
        roomId = this.roomId,
        startDate = this.startDate,
        endDate = this.endDate,
        guestName = this.guestName,
        guestEmail = this.guestEmail
    )
}