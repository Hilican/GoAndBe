package com.github.hilican.goandbe.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.data.Room.ItineraryItem
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.repo.implementations.HotelApiRepository
import kotlinx.coroutines.flow.StateFlow
import com.github.hilican.goandbe.repo.interfaces.IAuthRepository
import com.github.hilican.goandbe.repo.interfaces.ITripRepository
import com.github.hilican.goandbe.viewmodel.utils.deleteImageFromInternalStorage
import com.github.hilican.goandbe.viewmodel.utils.saveImageToInternalStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor (
    private val tripRepository: ITripRepository,
    private val authRepository: IAuthRepository,
    private val hotelApiRepository: HotelApiRepository,
) : ViewModel()
{
    val tripList: StateFlow<List<TripWithDetails>> = authRepository.getLogState()
        .flatMapLatest { userId ->
            if (userId != null) {
                // Si el usuario existe, le pedimos sus viajes al TripRepository
                tripRepository.getTripsForUser(userId)
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

            tripRepository.addTrip(newTrip)
        }
    }
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                // 1. Cancelamos las reservas en la API una por una
                val tripDetails = tripList.value.find { it.trip.tripId == trip.tripId }
                val reservationsToCancel = tripDetails?.reservations ?: emptyList()

                reservationsToCancel.forEach { reservation ->
                    try {
                        // Reemplaza por tu llamada real al repositorio
                        hotelApiRepository.cancelById(reservation.id)
                        Log.d("TripViewModel", "Cancelada en servidor la reserva: ${reservation.id}")
                    } catch (e: Exception) {
                        // Si falla internet para una reserva, logueamos pero seguimos con las demás
                        Log.e("TripViewModel", "Error al cancelar en API ${reservation.id}: ${e.message}")
                    }
                }

                trip.galleryImageUris.forEach { imagePath ->
                    val success = deleteImageFromInternalStorage(imagePath)
                    if (success) {
                        Log.d("TripViewModel", "Archivo eliminado: $imagePath")
                    } else {
                        Log.e("TripViewModel", "No se pudo eliminar el archivo: $imagePath")
                    }
                }

                tripRepository.deleteTrip(trip)
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error crítico en el proceso de borrado: ${e.message}")
            }
        }
    }
    // -- IMAGES --
    fun addImageToTrip(context: Context, tripId: Int, imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Guardar la imagen físicamente en el dispositivo (T3.2)
            val localPath = saveImageToInternalStorage(context, imageUri)

            if (localPath != null) {
                // 2. Buscar el viaje en Room
                val currentTrip = tripRepository.getTripById(tripId)

                if (currentTrip != null) {
                    // 3. Añadir la nueva ruta local a la lista existente
                    val updatedImages = currentTrip.galleryImageUris.toMutableList().apply {
                        add(localPath) // Guardamos la ruta del almacenamiento interno
                    }

                    // 4. Actualizar en la base de datos
                    val updatedTrip = currentTrip.copy(galleryImageUris = updatedImages)
                    tripRepository.editTrip(updatedTrip)
                }
            } else {
                Log.e("TripViewModel", "Error al guardar la imagen en el almacenamiento local")
            }
        }
    }

    fun deleteImage(tripId: Int, imagePath: String) {
        // Ejecutamos en el hilo IO para operaciones de disco y base de datos
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Buscamos el viaje actual directamente desde el repositorio/DAO
                val currentTrip = tripRepository.getTripById(tripId)
                // Nota: Si tu función en el repo se llama distinto (ej. getTrip), usa ese nombre.

                if (currentTrip != null) {

                    // 2. BORRADO FÍSICO: Eliminamos la foto del almacenamiento interno del teléfono
                    val fileDeleted = deleteImageFromInternalStorage(imagePath)
                    if (fileDeleted) {
                        Log.d("TripViewModel", "Archivo físico eliminado con éxito: $imagePath")
                    } else {
                        Log.e("TripViewModel", "No se pudo borrar el archivo físico o no existía: $imagePath")
                    }

                    // 3. ACTUALIZACIÓN EN ROOM: Creamos una nueva lista sin la imagen borrada
                    val updatedImages = currentTrip.galleryImageUris.toMutableList().apply {
                        remove(imagePath) // Quitamos la ruta exacta
                    }

                    // Creamos la copia del viaje con la lista de imágenes actualizada
                    val updatedTrip = currentTrip.copy(galleryImageUris = updatedImages)

                    // 4. Guardamos los cambios en Room usando el update que ya tenías
                    tripRepository.editTrip(updatedTrip)
                    Log.d("TripViewModel", "Viaje actualizado en Room sin la imagen.")
                } else {
                    Log.e("TripViewModel", "No se encontró el viaje con ID $tripId para borrar la imagen.")
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error al intentar eliminar la imagen: ${e.message}")
            }
        }
    }

    // -- ACTIVITIES --
    fun addActivityToTrip(tripId: Int, description: String, dateMillis: Long, cost: Long) {
        viewModelScope.launch {
            // 1. Creamos el objeto ItineraryItem
            // El ID no se pone porque Room lo genera solo
            val newActivity = ItineraryItem(
                parentTripId = tripId,
                description = description,
                activityTime = dateMillis,    // Seguimos usando Long para las fechas
                costEstimate = cost
            )

            // 2. Guardamos la actividad en su propia tabla
            tripRepository.addActivity(newActivity)

            // 3. Actualizamos el coste total en la tabla de Viajes
            // Buscamos el viaje, calculamos el nuevo coste y guardamos
            val existingTrip = tripRepository.getTripById(tripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = trip.totalCost + cost
                )
                tripRepository.editTrip(updatedTrip)
            }
        }
    }

    fun deleteActivity(tripId: Int, activity: ItineraryItem) {
        viewModelScope.launch {
            // Borramos la actividad de su tabla
            tripRepository.deleteActivity(activity)

            // Buscamos el viaje para actualizar su coste total
            val existingTrip = tripRepository.getTripById(tripId)

            existingTrip?.let { trip ->
                // Restamos el coste de la actividad eliminada
                val updatedTotalCost = (trip.totalCost - activity.costEstimate).coerceAtLeast(0L)

                val updatedTrip = trip.copy(
                    totalCost = updatedTotalCost
                )
                tripRepository.editTrip(updatedTrip)
            }
        }
    }
    fun updateActivity(
        newActivity: ItineraryItem,
        oldActivity: ItineraryItem, // actividad original (para saber su ID y coste antiguo)
    ) {
        viewModelScope.launch {
            // 1. Calculamos la diferencia de precio antes de modificar nada
            val costDifference = newActivity.costEstimate - oldActivity.costEstimate

            // 2. Creamos la versión actualizada de la actividad usando .copy()
            val updatedActivity = oldActivity.copy(
                description = newActivity.description,
                activityTime = newActivity.activityTime,
                costEstimate = newActivity.costEstimate,
            )

            // 3. Guardamos la actividad actualizada en la tabla de itinerarios
            tripRepository.addActivity(updatedActivity)

            // 4. Ajustamos el coste total del viaje
            val existingTrip = tripRepository.getTripById(oldActivity.parentTripId)
            existingTrip?.let { trip ->
                val updatedTrip = trip.copy(
                    totalCost = (trip.totalCost + costDifference).coerceAtLeast(0L)
                )
                tripRepository.editTrip(updatedTrip)
            }
        }
    }
}