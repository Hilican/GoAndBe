package com.github.hilican.goandbe.data.Room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.hilican.goandbe.domain.model.Address


//USER
@Entity(tableName = "users", indices = [
    Index(value = ["email"], unique = true),
    Index(value = ["username"], unique = true)
])
data class UserRoom(
    @PrimaryKey val userId: String, //UUID de Firebase
    val email: String,
    val username: String,
    val dateOfBirth: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    // Solucion para poner classes personalizadas, entra a la classe y coge los valores como String, Int....
    @Embedded(prefix = "address_")
    val address: Address,
    val phoneNumber : String,
    val receiveEmails : Boolean
)


//TRIPS
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val tripId: Int = 0,
    val userId: String, // para saber de qué usuario es
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val totalCost: Long,
    val createdAt: Long = System.currentTimeMillis(),

    val galleryImageUris: List<String> = emptyList()
)

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["tripId"],
            childColumns = ["parentTripId"],
            onDelete = ForeignKey.CASCADE // Si muere el padre, mueren los hijos
        )
    ],
    indices = [Index("parentTripId")] // Esto hace que las búsquedas sean más rápidas
)
data class ItineraryItem(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val parentTripId: Int,
    val description: String,
    val activityTime: Long,
    val costEstimate: Long,
)

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["tripId"],
            childColumns = ["parentTripId"],
            onDelete = ForeignKey.CASCADE // Si muere el padre, mueren los hijos
        )
    ],
    indices = [Index("parentTripId")] // Esto hace que las búsquedas sean más rápidas
)
data class ReservationRoom(
    @PrimaryKey val id: String,
    val parentTripId: Int,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,

    @Embedded(prefix = "hotel_")
    val hotel: HotelRoom,

    @Embedded(prefix = "room_")
    val room: RoomRoom
)

data class HotelRoom(
    val id: String,
    val name: String
    //val rating: Any
)
data class RoomRoom(
    val id: String,
    val roomType: String,
    val price: Float,
    val images: List<String>
)

// --- RELACIONES --

data class TripWithItinerary(
    @Embedded val trip: Trip,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "parentTripId"
    )
    val activities: List<ItineraryItem>,
)

data class TripWithReservation(
    @Embedded val trip: Trip,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "parentTripId"
    )
    val reservation: List<ReservationRoom>,
)

data class TripWithDetails(
    @Embedded val trip: Trip,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "parentTripId"
    )
    val activities: List<ItineraryItem>,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "parentTripId"
    )
    val reservations: List<ReservationRoom>
)

data class ReservationWithTrip(
    @Embedded val reservation: ReservationRoom,

    @Relation(
        parentColumn = "parentTripId",  // El ID del viaje que guardas en la reserva
        entityColumn = "tripId"         // La clave primaria en la tabla Trip
    )
    val trip: Trip
)