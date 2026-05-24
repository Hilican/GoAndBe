package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.MainDispatcherRule
import com.github.hilican.goandbe.repo.implementations.AuthRepository
import com.github.hilican.goandbe.repo.implementations.TripRepository
import com.github.hilican.goandbe.viewmodel.TripListViewModel
import com.github.hilican.goandbe.domain.TripMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TripListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val tripRepository: TripRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)

    private lateinit var viewModel: TripListViewModel

    // Usamos el userId en mockTrip
    private val testUserId = TripMocks.mockTrip.userId

    @Before
    fun setup() {
        // Configuramos los mocks por defecto
        every { authRepository.getLogState() } returns flowOf(testUserId)
        every { authRepository.getCurrentUserId() } returns testUserId

        // Por defecto, devolvemos la lista de viajes simulada
        every { tripRepository.getTripsForUser(testUserId) } returns flowOf(TripMocks.mockListTrips)

        viewModel = TripListViewModel(tripRepository, authRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `tripList - cuando el usuario esta logueado, carga sus viajes desde el repositorio`() = runTest {
        // GIVEN - El setup ya configuró mockListTrips por defecto

        // No entiendo muy bien esta parte del codigo, mas que nada porque no entiendo muy bien getLogState() del AuthRepository
        backgroundScope.launch {
            viewModel.tripList.collect {}
        }
        advanceUntilIdle()

        // WHEN
        val actualTrips = viewModel.tripList.value

        // THEN
        // Verificamos que la lista expuesta sea exactamente mockListTrips
        assert(actualTrips == TripMocks.mockListTrips)
    }

    @Test
    fun `addTrip - obtiene el id desde el AuthRepository y guarda el viaje`() = runTest {
        // GIVEN - Usamos los datos de mockTrip para la prueba
        val destination = TripMocks.mockTrip.name
        val start = TripMocks.mockTrip.startDate
        val end = TripMocks.mockTrip.endDate

        // WHEN
        viewModel.addTrip(destination, start, end)

        // THEN
        verify { authRepository.getCurrentUserId() }
        coVerify {
            tripRepository.addTrip(match { trip ->
                trip.userId == testUserId &&
                        trip.name == destination &&
                        trip.totalCost == 0L // Al crearse empieza en 0
            })
        }
    }

    @Test
    fun `deleteTrip - llama al repositorio para eliminar el viaje`() = runTest {
        // GIVEN - Usamos directamente mockTrip
        val tripToDelete = TripMocks.mockTrip

        // WHEN
        viewModel.deleteTrip(tripToDelete)

        // THEN
        coVerify { tripRepository.deleteTrip(tripToDelete) }
    }

    @Test
    fun `addActivityToTrip - guarda la actividad y actualiza sumando el coste al viaje`() = runTest {
        // GIVEN
        val tripId = TripMocks.mockTrip.tripId
        val activityCost = 100L

        // coste 1500L
        coEvery { tripRepository.getTripById(tripId) } returns TripMocks.mockTrip

        // WHEN
        viewModel.addActivityToTrip(tripId, "Nueva Actividad", 1714775586000L, cost = activityCost)

        // THEN
        coVerify { tripRepository.updateActivity(match { it.parentTripId == tripId }) }

        // El coste original (1500L) + la nueva actividad (100L) = 1600L
        coVerify { tripRepository.editTrip(match { it.totalCost == 1600L }) }
    }

    @Test
    fun `deleteActivity - elimina la actividad y resta su coste del coste total del viaje`() = runTest {
        // GIVEN
        val tripId = TripMocks.mockTrip.tripId
        coEvery { tripRepository.getTripById(tripId) } returns TripMocks.mockTrip

        // cuesta 50L
        val activityToDelete = TripMocks.mockActivitie

        // WHEN
        viewModel.deleteActivity(tripId, activityToDelete)

        // THEN
        coVerify { tripRepository.deleteActivity(activityToDelete) }

        // El coste original (1500L) - la actividad eliminada (50L) = 1450L
        coVerify { tripRepository.editTrip(match { it.totalCost == 1450L }) }
    }

    @Test
    fun `updateActivity - modifica la actividad y recalcula el coste del viaje usando la diferencia`() = runTest {
        // GIVEN
        val tripId = TripMocks.mockTrip.tripId
        coEvery { tripRepository.getTripById(tripId) } returns TripMocks.mockTrip

        // actividad mockActivitie cuesta 50L.
        // actualizarla para que cueste 120L (+70L)
        val oldActivity = TripMocks.mockActivitie
        val newCost = 120L

        // WHEN
        viewModel.updateActivity(tripId, oldActivity, "Templo Renovado", 1714775586000L, newCost)

        // THEN
        coVerify { tripRepository.addActivity(match { it.costEstimate == newCost }) }

        // El coste original (1500L) + la diferencia (+70L) = 1570L
        coVerify { tripRepository.editTrip(match { it.totalCost == 1570L }) }
    }
}