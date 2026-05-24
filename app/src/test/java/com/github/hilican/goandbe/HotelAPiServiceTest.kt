package com.github.hilican.goandbe

import com.github.hilican.goandbe.data.remote.api.IHotelApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // O el conversor que uses (Moshi, Kotlinx Serialization, etc.)

private val groupId = BuildConfig.GROUP_ID

class HotelApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: IHotelApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Creamos la instancia de Retrofit apuntando al servidor de prueba
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Usa la URL local del servidor falso
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IHotelApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun reserveRoom_success_parsesNestedJsonCorrectly() = runBlocking {
        // 1. Creamos un JSON simulando la respuesta del backend.
        // Fíjate cómo incluimos el objeto "reservation" y, dentro de él, "hotel" y "room".
        val jsonResponse = HotelTestData.jsonReserveRoomSuccess

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // 2. Creamos un request básico para enviar
        val request = HotelTestData.request

        // 3. Ejecutamos la petición
        val response = apiService.reserveRoom(groupId = "group-xyz", request = request)

        // 4. Asserts: Validamos que la estructura anidada se haya parseado sin romperse
        assertNotNull(response)
        assertEquals("Reserva confirmada con éxito", response.message)
        assertEquals(3, response.nights)

        // Validamos el objeto interno 'reservation'
        val reservation = response.reservation
        assertNotNull(reservation)
        assertEquals("res-101", reservation.id)
        assertEquals("Alex Gomez", reservation.guest_name)

        // Validamos que los DTOs más profundos (hotel y room) no hayan llegado nulos
        assertNotNull(reservation.hotel)
        assertNotNull(reservation.room)

        // 5. Validamos que la URL se construyó con el groupId correcto en el Path
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/hotels/group-xyz/reserve", recordedRequest.path)
    }

    @Test
    fun getAvailability_withAllParameters_buildsCorrectUrlAndParsesData() = runBlocking {
        // 1. Preparar el servidor falso
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(HotelTestData.jsonAvailabilitySuccess)
        )

        // 2. Ejecutar pasando TODOS los parámetros
        val response = apiService.getAvailability(
            groupId = "G01",
            startDate = "2026-06-01",
            endDate = "2026-06-10",
            hotelId = "hotel-01",
            city = "Barcelona"
        )

        // 3. Verificar que la URL contenga todas las Query Parameters correctamente codificadas
        val recordedRequest = mockWebServer.takeRequest()
        val expectedPath = "/hotels/G01/availability?start_date=2026-06-01&end_date=2026-06-10&hotel_id=hotel-01&city=Barcelona"
        assertEquals(expectedPath, recordedRequest.path)

        // 4. Verificar el mapeo de los DTOs
        assertNotNull(response)
        assertEquals(1, response.available_hotels.size)

        val hotel = response.available_hotels[0]
        assertEquals("hotel-01", hotel.id)
        assertEquals("Hotel Paraíso", hotel.name)
        assertNotNull(hotel.rooms) // Comprobar que la lista de habitaciones se inicializó (aunque esté vacía)
    }

    @Test
    fun getAvailability_withNullOptionalParameters_excludesThemFromUrl() = runBlocking {
        // 1. Preparar el servidor falso
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(HotelTestData.jsonAvailabilitySuccess)
        )

        // 2. Ejecutar pasando únicamente los parámetros obligatorios
        apiService.getAvailability(
            groupId = "G01",
            startDate = "2026-06-01",
            endDate = "2026-06-10",
            hotelId = null,  // Opcional en null
            city = null      // Opcional en null
        )

        // 3. Verificar que la URL NO contiene las llaves 'hotel_id' ni 'city'
        val recordedRequest = mockWebServer.takeRequest()
        val expectedCleanPath = "/hotels/G01/availability?start_date=2026-06-01&end_date=2026-06-10"

        assertEquals(expectedCleanPath, recordedRequest.path)
    }
}