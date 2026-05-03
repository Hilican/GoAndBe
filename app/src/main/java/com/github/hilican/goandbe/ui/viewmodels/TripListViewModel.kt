package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripWithItinerary
import kotlinx.coroutines.flow.StateFlow
import com.github.hilican.goandbe.domain.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripListViewModel(private val repository: TripRepository) : ViewModel()
{
    private val userId: String = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val tripList: StateFlow<List<TripWithItinerary>> = repository.getTripsForUser(userId)
        .stateIn(
            scope = viewModelScope,
            // El famoso "stop timeout" de 5 segundos
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTrip(destination: String, startDate: Long, endDate: Long) {
        // 1. Obtenemos el usuario actual de Firebase
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 2. Ejecutamos en una corrutina (viewModelScope) porque el repo es suspend
        viewModelScope.launch {
            val newTrip = Trip(
                userId = currentUserId,
                name = destination,
                startDate = startDate,
                endDate = endDate,
                totalCost = 0L
            )

            repository.addTrip(newTrip)
        }
    }
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    fun addActivityToTrip(tripId: Int, description: String, dateMillis: Long, cost: Long) {
        viewModelScope.launch {
            // 1. Creamos el objeto ItineraryItem
            // El ID no se pone (es 0) porque Room lo genera solo
            val newActivity = ItineraryItem(
                parentTripId = tripId, // El "pegamento" que une la actividad al viaje
                description = description,
                activityTime = dateMillis,    // Seguimos usando Long para las fechas
                costEstimate = cost
            )

            // 2. Guardamos la actividad en su propia tabla
            repository.updateActivity(newActivity)

            // 3. Actualizamos el coste total en la tabla de Viajes
            // Buscamos el viaje, calculamos el nuevo coste y guardamos
            val existingTrip = repository.getTripById(tripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = trip.totalCost + cost
                )
                repository.editTrip(updatedTrip)
            }
        }
    }

    fun deleteActivity(tripId: Int, activity: ItineraryItem) {
        viewModelScope.launch {
            // 1. Borramos la actividad de su tabla
            repository.deleteActivity(activity)

            // 2. Buscamos el viaje para actualizar su coste total
            val existingTrip = repository.getTripById(tripId)

            existingTrip?.let { trip ->
                // 3. Restamos el coste de la actividad eliminada
                val updatedTotalCost = (trip.totalCost - activity.costEstimate).coerceAtLeast(0L)

                val updatedTrip = trip.copy(
                    totalCost = updatedTotalCost
                )

                // 4. Guardamos el viaje actualizado
                repository.editTrip(updatedTrip)
            }

            // La UI se actualizará sola gracias al Flow
        }
    }

    fun updateActivity(
        tripId: Int,
        activity: ItineraryItem, // actividad original (para saber su ID y coste antiguo)
        newName: String,
        newDate: Long,
        newCost: Long
    ) {
        viewModelScope.launch {
            // 1. Calculamos la diferencia de precio antes de modificar nada
            val costDifference = newCost - activity.costEstimate

            // 2. Creamos la versión actualizada de la actividad usando .copy()
            val updatedActivity = activity.copy(
                description = newName,
                activityTime = newDate,
                costEstimate = newCost
            )

            // 3. Guardamos la actividad actualizada en la tabla de itinerarios
            repository.addActivity(updatedActivity)

            // 4. Ajustamos el coste total del viaje
            val existingTrip = repository.getTripById(tripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = (trip.totalCost + costDifference).coerceAtLeast(0L)
                )
                repository.editTrip(updatedTrip)
            }
        }
    }

    private fun dateToLong(dateString: String): Long {
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            sdf.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}