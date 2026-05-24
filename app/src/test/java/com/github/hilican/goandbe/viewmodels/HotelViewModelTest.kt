package com.github.hilican.goandbe.viewmodels

import android.util.Log
import com.github.hilican.goandbe.HotelTestData
import com.github.hilican.goandbe.MainDispatcherRule
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.domain.HotelMock
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.domain.iRepositories.IAuthRepository
import com.github.hilican.goandbe.domain.iRepositories.IHotelApiRepository
import com.github.hilican.goandbe.domain.iRepositories.ITripRepository
import com.github.hilican.goandbe.domain.convertMillisToString
import com.github.hilican.goandbe.ui.viewmodels.HotelViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HotelViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HotelViewModel
    private val authRepository: IAuthRepository = mockk(relaxed = true)
    private val tripRepository: ITripRepository = mockk(relaxed = true)
    private val repository: IHotelApiRepository = mockk(relaxed = true)

    private val authStateFlow = MutableStateFlow<String?>(null)

    @Before
    fun setup() {
        // Configuramos el mock para que getLogState devuelva un flow vacío por defecto
        every { authRepository.getLogState() } returns authStateFlow
        every { authRepository.getCurrentUserId() } returns null

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        viewModel = HotelViewModel(repository, tripRepository, authRepository)
    }

    @Test
    fun `reservationsList emite la lista del usuario cuando se inicia sesion`() = runTest {
        val userId = "user_abc"
        val reserves = HotelMock.mockListReservation

        // Cuando el ViewModel pregunte por los viajes de este ID, devolvemos la lista de prueba
        every { tripRepository.getUserReserves(userId) } returns flowOf(reserves)

        // 2. Crear una lista para capturar los valores que emita el StateFlow
        val results = mutableListOf<List<ReservationRoom>>()

        // 3. Empezar a escuchar el flujo en el backgroundScope del test.
        // UnconfinedTestDispatcher para que capture los valores de inmediato.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.reservationsList.collect { results.add(it) }
        }

        // En este punto, como el collect ya se ejecutó una vez,
        // la lista ya contiene el valor inicial.
        assertEquals(emptyList<ReservationRoom>(), results[0])

        // 4. Simulamos el login cambiando el valor del flujo de auth
        authStateFlow.value = userId

        // 5. Verificamos que se añadió un nuevo elemento a nuestros resultados
        assertEquals(2, results.size) // El estado inicial + la nueva lista
        assertEquals(reserves, results[1]) // El segundo elemento son las reservas
    }

    @Test
    fun `reservationsList vuelve a estar vacia si el usuario cierra sesion`() = runTest {
        // 1. PREPARAR: El usuario inicia sesión y tiene reservas cargadas
        val userId = "user_abc"
        val reserves = HotelMock.mockListReservation
        every { tripRepository.getUserReserves(userId) } returns flowOf(reserves)

        // 2. CAPTURAR: Creamos la lista para capturar el historial de estados
        val results = mutableListOf<List<ReservationRoom>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.reservationsList.collect { results.add(it) }
        }

        // [Estado 0]: Lista vacía inicial por defecto
        assertEquals(emptyList<ReservationRoom>(), results[0])

        // Simulamos el inicio de sesión
        authStateFlow.value = userId

        // [Estado 1]: La lista ahora contiene las reservas del usuario
        assertEquals(reserves, results[1])

        // 3. ACTUAR: Simulamos el cierre de sesión (ponemos el flujo de auth en null)
        authStateFlow.value = null

        // 4. VERIFICAR: Comprobamos que se ha añadido un nuevo estado al historial
        // y que ese estado vuelve a ser una lista vacía.
        assertEquals(3, results.size) // Inicial (vacio) -> Logueado (reservas) -> Deslogueado (vacio)
        assertEquals(emptyList<ReservationRoom>(), results[2])
    }

    @Test
    fun `createReserve en caso de EXITO oculta el loading y guarda la reserva`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        val hotelId = "hotel_abc"

        // Mockear al usuario de Firebase
        every { authRepository.getCurrentUserId() } returns TripMocks.mockTrip.userId
        // NOTA: Cambia "User" por el nombre real de tu modelo de usuario
        coEvery { authRepository.getUserById(TripMocks.mockTrip.userId) } returns mockk(relaxed = true) {
            every { username } returns "Pepe"
            every { email } returns "pepe@correo.com"
        }

        // Mockear respuesta de la API (Retrofit)
        val mockReserveResponse = HotelMock.mockReservation
        coEvery { repository.reserveByGroupId(any(), any()) } returns mockReserveResponse

        // Mockear guardado en base de datos local (Room)
        coEvery { tripRepository.addReserve(any()) } returns Result.success(1L)

        // 2. EJECUCIÓN (Act)
        viewModel.createReserve(
            room = HotelMock.mockRoom, // o mockRoom.toDomainModel() si Room y RoomRoom son distintos
            start = TripMocks.mockTrip.startDate,
            end = TripMocks.mockTrip.endDate,
            hotelId = hotelId,
            tripId = TripMocks.mockTrip.tripId
        )

        // Esperamos a que el viewModelScope.launch termine su trabajo
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value // Asumiendo que expones un uiState

        assertEquals(false, state.isLoading)
        assertEquals(null, state.errorMessage)

        // Opcional: Verificar que las funciones realmente se llamaron
        coVerify(exactly = 1) { repository.reserveByGroupId(any(), any()) }
        coVerify(exactly = 1) { tripRepository.addReserve(any()) }
    }

    @Test
    fun `cancelReserve en caso de EXITO oculta el loading y cancela la reserva en la API`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        val reserveId = "res_999"
        // Usamos el mock de reserva que creamos anteriormente
        val mockReservation = HotelMock.mockReservationRoom

        // Simulamos que la base de datos local encuentra la reserva con éxito
        coEvery { tripRepository.getReservationById(reserveId) } returns Result.success(mockReservation)

        // Simulamos que la API cancela la reserva con éxito (devolvemos un String de confirmación o lo que devuelva tu API)
        coEvery { repository.cancelByGroupId(any(), any()) } returns "Reserva cancelada correctamente"

        // 2. EJECUCIÓN (Act)
        viewModel.cancelReserve(reserveId)

        // Aceleramos el tiempo virtual para que la corrutina complete todo el bloque try/catch
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals(null, state.errorMessage)

        // Verificamos que se llamó al repositorio local y luego a la API
        coVerify(exactly = 1) { tripRepository.getReservationById(reserveId) }
        coVerify(exactly = 1) { repository.cancelByGroupId(any(), any()) }
    }

    @Test
    fun `cancelReserve cuando NO SE ENCUENTRA la reserva local muestra mensaje de error y NO llama a la API`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        val reserveId = "res_fantasma"

        // Simulamos que la base de datos local falla al buscar la reserva (devuelve un Failure)
        coEvery { tripRepository.getReservationById(reserveId) } returns Result.failure(Exception("Not found"))

        // 2. EJECUCIÓN (Act)
        viewModel.cancelReserve(reserveId)

        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals("No se pudo cargar el detalle de la reserva.", state.errorMessage)

        // Verificamos que se intentó buscar de forma local...
        coVerify(exactly = 1) { tripRepository.getReservationById(reserveId) }
        // ...pero que JAMÁS se llegó a llamar a la API externa porque el flujo entró en el .onFailure
        coVerify(exactly = 0) { repository.cancelByGroupId(any(), any()) }
    }

    @Test
    fun `loadHotels en caso de EXITO actualiza el uiState con la lista de hoteles`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        // Creamos una lista simulada usando el mockHotel de tus datos de prueba
        val mockHotelsList = listOf(HotelMock.mockHotel)

        // Le decimos al repositorio que cuando llame a getHotels devuelva nuestra lista
        coEvery { repository.getHotels(any()) } returns mockHotelsList

        // 2. EJECUCIÓN (Act)
        viewModel.loadHotels()

        // Esperamos a que termine la corrutina del viewModelScope
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals(null, state.errorMessage)
        assertEquals(mockHotelsList, state.hotels) // Verificamos que los hoteles guardados son los del mock
    }

    @Test
    fun `loadHotels en caso de ERROR de red captura la excepcion y muestra mensaje`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        // Simulamos que el repositorio lanza un error (por ejemplo, sin internet)
        coEvery { repository.getHotels(any()) } throws Exception("No internet")

        // 2. EJECUCIÓN (Act)
        viewModel.loadHotels()
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals("No se pudieron cargar los hoteles.", state.errorMessage)
        // La lista de hoteles debería estar vacía o mantener su estado previo
    }

    @Test
    fun `checkAvailability en caso de EXITO mapea fechas y guarda hoteles disponibles`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        val startMillis = 1714775586000L // Fecha de prueba en Long
        val endMillis = 1714948386000L
        val expectedStartDateStr = convertMillisToString(startMillis) // El formato String esperado
        val expectedEndDateStr = convertMillisToString(endMillis)

        val mockAvailableHotels = listOf(HotelMock.mockHotel)

        // Configuramos el mock para que responda cuando reciba exactamente esos Strings formateados
        coEvery {
            repository.getAvailability(
                groupId = any(),
                start = expectedStartDateStr,
                end = expectedEndDateStr,
                hotelId = null,
                city = "Tokio"
            )
        } returns mockAvailableHotels

        // 2. EJECUCIÓN (Act)
        viewModel.checkAvailability(
            start = startMillis,
            end = endMillis,
            hotelId = null,
            city = "Tokio"
        )
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals(null, state.errorMessage)
        assertEquals(mockAvailableHotels, state.availableHotels)

        // Verificación extra: Asegurarnos de que el repositorio recibió las fechas bien formateadas
        coVerify(exactly = 1) {
            repository.getAvailability(any(), expectedStartDateStr, expectedEndDateStr, null, "Tokio")
        }
    }

    @Test
    fun `checkAvailability en caso de FALLO oculta loading y muestra mensaje de error`() = runTest {
        // 1. PREPARACIÓN (Arrange)
        coEvery {
            repository.getAvailability(any(), any(), any(), any(), any())
        } throws Exception("Error de servidor")

        // 2. EJECUCIÓN (Act)
        viewModel.checkAvailability(
            start = 1714775586000L,
            end = 1714948386000L,
            hotelId = null,
            city = null
        )
        advanceUntilIdle()

        // 3. VERIFICACIÓN (Assert)
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals("No se pudo comprobar la disponibilidad en esas fechas.", state.errorMessage)
    }
}