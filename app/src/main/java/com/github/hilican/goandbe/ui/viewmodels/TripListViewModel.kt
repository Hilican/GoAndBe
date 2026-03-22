package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.github.hilican.goandbe.domain.*

class TripListViewModel(private val repository: TripRepository = TripRepository()) : ViewModel() {

    // Holds the current state of the trip list for the UI to observe
    private val _tripList = MutableStateFlow<List<Trip>>(emptyList())
    val tripList: StateFlow<List<Trip>> = _tripList.asStateFlow()
    var nextId: Int = 0

    init {
        // Load initial data (empty in this case, but good practice)
        refreshTrips()
    }

    private fun refreshTrips() {
        // Update the StateFlow with the latest data from the repository
        _tripList.value = repository.getAllTrips()
    }

    fun addTrip(destination: String, startDate: String, endDate: String ) {
        val newTrip = Trip(
            id = nextId,
            name = destination,
            startDate = startDate,
            endDate = endDate,
        )
        nextId++
        repository.addTrip(newTrip)
        refreshTrips()
    }

    fun editTripCost(tripId: Int, newCost: Long) {
        val existingTrip = repository.getTripById(tripId)

        if (existingTrip != null) {
            // .copy() clona el objeto pero cambia solo el dato que le digas
            val updatedTrip = existingTrip.copy(totalCost = newCost)
            repository.editTrip(updatedTrip)
            refreshTrips()
        }
    }

    fun deleteTrip(tripId: Int) {
        repository.deleteTrip(tripId)
        refreshTrips()
    }

    fun addActivityToTrip(tripId: Int, description: String, dateMillis: Long, cost: Int) {
        // 1. Buscamos el viaje al que queremos añadir la actividad
        val existingTrip = repository.getTripById(tripId)

        if (existingTrip != null) {
            // 2. Creamos el nuevo objeto ItineraryItem
            val newActivity = ItineraryItem(
                id = repository.getNewActivityId(), // ID único
                description = description,
                activityTime = java.util.Date(dateMillis), // Convertimos los milisegundos a Date
                costEstimate = cost.toDouble() // Convertimos el Int del diálogo a Double
            )

            // 3. Creamos una NUEVA lista combinando las actividades anteriores con la nueva
            val updatedActivities = existingTrip.activities + newActivity

            // 4. Sumamos el coste de la actividad al coste total del viaje
            val updatedTotalCost = existingTrip.totalCost + cost.toLong()

            // 5. Usamos .copy() para crear un nuevo Viaje con la lista y el coste actualizados
            val updatedTrip = existingTrip.copy(
                activities = updatedActivities,
                totalCost = updatedTotalCost
            )

            // 6. Guardamos los cambios y actualizamos la UI
            repository.editTrip(updatedTrip)
            refreshTrips()
        }

    }

    fun deleteActivityFromTrip(tripId: Int, activityId: Int) {
        // 1. Buscamos el viaje en el repositorio
        val trip = repository.getTripById(tripId) ?: return

        // 2. Filtramos la lista para quedarnos con todas MENOS la que queremos borrar
        val updatedActivities = trip.activities.filter { it.id != activityId }

        // 3. Recalculamos el coste total del viaje sumando las actividades restantes
        val newTotalCost = updatedActivities.sumOf { it.costEstimate }.toLong()

        // 4. Creamos una copia del viaje con la nueva lista y el nuevo coste
        val updatedTrip = trip.copy(
            activities = updatedActivities,
            totalCost = newTotalCost
        )

        // 5. Guardamos en el repositorio y actualizamos el estado de la UI
        repository.editTrip(updatedTrip)

        // Actualizar tu StateFlow de viajes
        refreshTrips()
    }
}