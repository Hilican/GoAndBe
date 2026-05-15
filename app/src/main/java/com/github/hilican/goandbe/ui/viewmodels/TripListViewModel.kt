package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.data.ItineraryItem
import com.github.hilican.goandbe.data.Trip
import com.github.hilican.goandbe.data.TripWithItinerary
import com.github.hilican.goandbe.data.repositories.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import com.github.hilican.goandbe.data.repositories.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor (
    private val triprepository: TripRepository,
    private val authRepository: AuthRepository
) : ViewModel()
{
    val tripList: StateFlow<List<TripWithItinerary>> = authRepository.getLogState()
        .flatMapLatest { userId ->
            if (userId != null) {
                // Si el usuario existe, le pedimos sus viajes al TripRepository
                triprepository.getTripsForUser(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun addTrip(destination: String, startDate: Long, endDate: Long) {
        // 1. Obtenemos el usuario actual de Firebase
        val currentUserId = authRepository.getCurrentUserId() ?: ""

        // 2. Ejecutamos en una corrutina (viewModelScope) porque el repo es suspend
        viewModelScope.launch {
            val newTrip = Trip(
                userId = currentUserId,
                name = destination,
                startDate = startDate,
                endDate = endDate,
                totalCost = 0L
            )

            triprepository.addTrip(newTrip)
        }
    }
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            triprepository.deleteTrip(trip)
        }
    }

    // -- ACTIVITIES --
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
            triprepository.updateActivity(newActivity)

            // 3. Actualizamos el coste total en la tabla de Viajes
            // Buscamos el viaje, calculamos el nuevo coste y guardamos
            val existingTrip = triprepository.getTripById(tripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = trip.totalCost + cost
                )
                triprepository.editTrip(updatedTrip)
            }
        }
    }

    fun deleteActivity(tripId: Int, activity: ItineraryItem) {
        viewModelScope.launch {
            // Borramos la actividad de su tabla
            triprepository.deleteActivity(activity)

            // Buscamos el viaje para actualizar su coste total
            val existingTrip = triprepository.getTripById(tripId)

            existingTrip?.let { trip ->
                // Restamos el coste de la actividad eliminada
                val updatedTotalCost = (trip.totalCost - activity.costEstimate).coerceAtLeast(0L)

                val updatedTrip = trip.copy(
                    totalCost = updatedTotalCost
                )

                triprepository.editTrip(updatedTrip)
            }
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
            triprepository.addActivity(updatedActivity)

            // 4. Ajustamos el coste total del viaje
            val existingTrip = triprepository.getTripById(tripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = (trip.totalCost + costDifference).coerceAtLeast(0L)
                )
                triprepository.editTrip(updatedTrip)
            }
        }
    }
}