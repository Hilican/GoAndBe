package com.github.hilican.goandbe.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.hilican.goandbe.domain.Address

@Entity(tableName = "users", indices = [
    Index(value = ["email"], unique = true),
    Index(value = ["username"], unique = true)
])
data class User(
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

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val tripId: Int = 0,
    val userId: String, // para saber de qué usuario es
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val totalCost: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["tripId"],
            childColumns = ["parentTripId"],
            onDelete = ForeignKey.CASCADE // ¡Si muere el padre, mueren los hijos!
        )
    ],
    indices = [Index("parentTripId")] // Esto hace que las búsquedas sean más rápidas
)
data class ItineraryItem(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val parentTripId: Int,
    val description: String,
    val activityTime: Long, // Date() convertido a milisegundos
    val costEstimate: Long
)

data class TripWithItinerary(
    @Embedded val trip: Trip,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "parentTripId"
    )
    val activities: List<ItineraryItem>
)