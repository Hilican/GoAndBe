package com.github.hilican.goandbe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.repo.interfaces.IAuthRepository
import com.github.hilican.goandbe.repo.interfaces.IHotelApiRepository
import com.github.hilican.goandbe.repo.interfaces.ITripRepository
import com.github.hilican.goandbe.domain.model.ReserveRequest
import com.github.hilican.goandbe.domain.*
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.viewmodel.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class HotelUiState(
    val isLoading: Boolean = true,    // Cargando inicial (true por defecto)
    val errorMessage: String? = null,  // Errores sueltos
    val hotels: List<Hotel> = emptyList(),
    val availableHotels: List<Hotel> = emptyList(),
    // to confirm operations
    val hotelId: String = "",
    val doe: Long = 0L,
    val dod: Long = 0L,
    val tripId: Int = -2,       //-2 = No se ha escodigo, -1 Para un futuro indicar que se quiere crear un nuevo trip con la reserva
)

@HiltViewModel
class HotelViewModel @Inject constructor (
    private val repository: IHotelApiRepository,
    private val tripRepository: ITripRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {
    private val groupId = BuildConfig.GROUP_ID

    private val _uiState = MutableStateFlow(HotelUiState()) // Toma los valores por defecto
    val uiState: StateFlow<HotelUiState> = _uiState

    val reservationsList: StateFlow<List<ReservationRoom>> = authRepository.getLogState()
        .flatMapLatest { userId ->
            if (userId != null) {
                // Si el usuario existe, le pedimos sus viajes al TripRepository
                tripRepository.getUserReserves(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadHotels() {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                val hotelsList = repository.getHotels(groupId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hotels = hotelsList
                    )
                }
            } catch (e: Exception) {
                Log.e("HotelViewModel", "Error cargando hoteles: ${e.localizedMessage}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudieron cargar los hoteles."
                    )
                }
            }
        }
    }

    fun setTripId(tripId: Int) {
        _uiState.update {
            it.copy(tripId = tripId)
        }
    }

    fun clearTripId() {
        _uiState.update {
            it.copy(tripId = -2)
        }
    }

    fun clearAvailableHotels() {
        _uiState.update {
            it.copy(availableHotels = emptyList())
        }
    }

    fun setHotelId(hotelId: String) {
        _uiState.update {
            it.copy(hotelId = hotelId)
        }
    }
    fun clearHotelId() {
        _uiState.update {
            it.copy(hotelId = "")
        }
    }
    fun checkAvailability(
        start: Long,
        end: Long,
        hotelId: String? = null,
        city: String? = null
    ) {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                val startDateStr = (start / 1000).toString()
                val endDateStr = (end / 1000).toString()
                Log.d("HotelViewModel", "Enviando fechas al servidor -> Start: $startDateStr, End: $endDateStr")
                // 2. Llamamos al repositorio
                val availableList = repository.getAvailability(
                    groupId = groupId,
                    start = startDateStr,
                    end = endDateStr,
                    hotelId = hotelId,
                    city = city
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        availableHotels = availableList,
                        doe = start,
                        dod = end,
                    )
                }
                if(hotelId != null)
                {
                    _uiState.update {
                        it.copy(
                            hotelId = hotelId
                        )
                    }
                }
            } catch (e: HttpException) {
                // 1. Usamos tu utilidad para sacar el mensaje limpio que envía el servidor
                val parsedErrorMessage = ErrorUtils.extractErrorMessage(e)

                // 2. Lo dejamos en el Logcat para que lo sigas viendo en consola con detalle
                Log.e("HotelViewModel", "Código HTTP ${e.code()} - Error parseado: $parsedErrorMessage")

                // 3. Actualizamos la UI con el mensaje real para saber qué campo está rechazando el backend
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = parsedErrorMessage // En la pantalla verás exactamente qué parámetro falla
                    )
                }
            } catch (e: Exception) {
                Log.e("HotelViewModel", "Error general de red/código: ${e.localizedMessage}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo comprobar la disponibilidad."
                    )
                }
            }
        }
    }

    fun createReserve(roomId: String) {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }
        viewModelScope.launch {
            // 1. Obtenemos el usuario actual de Firebase
            val currentUserId = authRepository.getCurrentUserId() ?: ""
            if (currentUserId.isEmpty()) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Usuario no autenticado en Firebase.")
                }
                return@launch
            }
            val user = authRepository.getUserById(currentUserId)
            if (user == null) {
                // lógica de error
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "No se ha encontrado el usuario")
                }
                return@launch
            }
            val startDateStr = (uiState.value.doe / 1000).toString()
            val endDateStr = (uiState.value.dod / 1000).toString()

            val req = ReserveRequest(
                hotelId = uiState.value.hotelId,
                roomId = roomId,
                startDate = startDateStr,
                endDate = endDateStr,
                guestName = user.username,
                guestEmail = user.email,
            )

            try {
                val reserve = repository.reserveByGroupId(groupId, req)
                val localHotel = uiState.value.availableHotels.find { it.id == uiState.value.hotelId }
                val localRoom = localHotel?.rooms?.find { it.id == roomId }

                val enrichedReserve = reserve.copy(
                    hotel = localHotel ?: reserve.hotel,
                    room = localRoom ?: reserve.room
                )

                val toAdd = enrichedReserve.toRoomEntity(uiState.value.tripId)
                val result = tripRepository.addReserve(toAdd)
                result.onSuccess { rowId ->
                    // 🌟 NUEVO: Si la reserva se guardó bien localmente, actualizamos el coste del viaje
                    val tripId = uiState.value.tripId
                    val existingTrip = tripRepository.getTripById(tripId)

                    existingTrip?.let { trip ->
                        // 1. Calculamos las noches totales de la estancia
                        val totalNights = if (uiState.value.dod > uiState.value.doe) {
                            ((uiState.value.dod - uiState.value.doe) / (1000 * 60 * 60 * 24)).toInt()
                        } else {
                            1
                        }

                        // 2. Calculamos el coste total de esta reserva (Precio x Noches)
                        val roomPrice = localRoom?.price ?: 0f
                        val reservationCost = (roomPrice * totalNights).toLong() // Convertimos al tipo de tu totalCost (ej: Long)

                        // 3. Sumamos el nuevo coste al coste que ya tenía el viaje
                        val updatedTotalCost = trip.totalCost + reservationCost

                        // 4. Guardamos el viaje editado en Room
                        val updatedTrip = trip.copy(totalCost = updatedTotalCost)
                        tripRepository.editTrip(updatedTrip)
                    }
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    setTripId(-2)
                }.onFailure { exception ->
                    Log.e("TripViewModel", "Fallo al guardar: ${exception.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo guardar la reserva en tu dispositivo."
                        )
                    }
                }
            } catch (e: HttpException) {
                val decodedError = ErrorUtils.extractErrorMessage(e)
                Log.e("BookViewModel", "HTTP error: ${decodedError}  $e")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = decodedError)
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error: ${e.localizedMessage}")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de conexión. Inténtelo de nuevo.")
                }
            }
        }
    }

    fun cancelReserve(reserveId: String) {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null)
        }
        // Ejecutamos en una corrutina (viewModelScope) porque el repo es suspend
        viewModelScope.launch {
            try {
                //Lo complico un poco simplemente para no usar lo de "admin" (deleteById)
                val result = tripRepository.getReservationById(reserveId)
                result.onSuccess { reserve ->
                    val msg = repository.cancelByGroupId(groupId,reserve.toRequest())
                    // 3. 📉 ACTUALIZACIÓN DEL COSTE DEL VIAJE
                    // Buscamos el viaje padre al que pertenece esta reserva
                    val existingTrip = tripRepository.getTripById(reserve.parentTripId)

                    existingTrip?.let { trip ->
                        // Convertimos las fechas de la reserva (que están en String/milisegundos) a Long
                        val startMillis = reserve.startDate.toLongOrNull() ?: 0L
                        val endMillis = reserve.endDate.toLongOrNull() ?: 0L

                        // Calculamos las noches totales que duraba la reserva
                        val totalNights = ((endMillis - startMillis) / (1000 * 60 * 60 * 24)).toInt()
                        // Calculamos cuánto costó esta reserva en su momento (Precio por noche x Noches)
                        // Si tu modelo local guarda el precio como Double/Float, lo casteamos a Long
                        val reservationCost = (reserve.room.price * totalNights).toLong()

                        // Restamos el coste de la reserva al total del viaje (evitando que baje de 0)
                        val updatedTotalCost = (trip.totalCost - reservationCost).coerceAtLeast(0L)

                        // Guardamos el viaje modificado con el nuevo coste en Room
                        val updatedTrip = trip.copy(totalCost = updatedTotalCost)
                        tripRepository.editTrip(updatedTrip)
                    }
                    _uiState.update {
                            it.copy(isLoading = false, errorMessage = null)
                        }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No se pudo cargar el detalle de la reserva.")
                    }
                }
            } catch (e: HttpException) {
                val decodedError = ErrorUtils.extractErrorMessage(e)
                Log.e("BookViewModel", "HTTP error: ${decodedError}  $e")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = decodedError)
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error: ${e.localizedMessage}")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de conexión. Inténtelo de nuevo.")
                }
            }
        }
    }
}