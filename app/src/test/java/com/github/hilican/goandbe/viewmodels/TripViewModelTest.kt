package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.MainDispatcherRule
import com.github.hilican.goandbe.repo.implementations.AuthRepository
import com.github.hilican.goandbe.repo.implementations.TripRepository
import com.github.hilican.goandbe.viewmodel.TripListViewModel
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.repo.implementations.HotelApiRepository
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
    private val hotelApiRepository: HotelApiRepository = mockk(relaxed = true)

    private lateinit var viewModel: TripListViewModel

    // Usamos el userId en mockTrip
    private val testUserId = TripMocks.mockTrip.userId

    @Before
    fun setup() {
        // Configuramos los mocks por defecto
        every { authRepository.getLogState() } returns flowOf(testUserId)
        every { authRepository.getCurrentUserId() } returns testUserId

        // Por defecto, devolvemos la lista de viajes simulada
        every { tripRepository.getTripsForUser(testUserId) } returns flowOf(TripMocks.mockListTripsWithDetails)

        viewModel = TripListViewModel(tripRepository, authRepository, hotelApiRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `tripList - cuando el usuario esta logueado, carga sus viajes con detalles desde el repositorio`() = runTest {
        backgroundScope.launch {
            viewModel.tripList.collect {}
        }
        advanceUntilIdle()

        // WHEN
        val actualTrips = viewModel.tripList.value

        // THEN
        // Verificamos que coincida con la lista simulada que contiene los detalles (reservas, actividades)
        assert(actualTrips == TripMocks.mockListTripsWithDetails)
    }

    @Test
    fun `addTrip - obtiene el id desde el AuthRepository y guarda el viaje`() = runTest {
        // GIVEN
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
                        trip.totalCost == 0L
            })
        }
    }

    @Test
    fun `deleteTrip - cancela reservas en API y luego llama al repositorio local para eliminar el viaje`() = runTest {
        // GIVEN
        val tripToDelete = TripMocks.mockTrip

        // WHEN
        viewModel.deleteTrip(tripToDelete)

        // THEN
        // Verificamos que se llame al borrado final del viaje local
        coVerify { tripRepository.deleteTrip(tripToDelete) }
    }

    @Test
    fun `addActivityToTrip - guarda la actividad nueva y actualiza sumando el coste al viaje`() = runTest {
        // GIVEN
        val tripId = TripMocks.mockTrip.tripId
        val activityCost = 100L
        coEvery { tripRepository.getTripById(tripId) } returns TripMocks.mockTrip

        // WHEN
        viewModel.addActivityToTrip(tripId, "Nueva Actividad", 1714775586000L, cost = activityCost)

        // THEN
        // El nuevo ViewModel usa addActivity en lugar de updateActivity para crear
        coVerify { tripRepository.addActivity(match { it.parentTripId == tripId && it.costEstimate == activityCost }) }

        // El coste original (1500L) + la nueva actividad (100L) = 1600L
        coVerify { tripRepository.editTrip(match { it.totalCost == 1600L }) }
    }
    @Test
    fun `deleteActivity - elimina la actividad y resta su coste del coste total del viaje`() = runTest {
        // GIVEN
        val tripId = TripMocks.mockTrip.tripId
        coEvery { tripRepository.getTripById(tripId) } returns TripMocks.mockTrip

        val activityToDelete = TripMocks.mockActivitie // Cuesta 50L

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

        // Creamos los objetos exactos que requiere la nueva firma de la función
        val oldActivity = TripMocks.mockActivitie // Cuesta 50L
        val newCost = 120L
        val newActivity = oldActivity.copy(
            description = "Templo Renovado",
            activityTime = 1714775586000L,
            costEstimate = newCost
        )

        // WHEN
        viewModel.updateActivity(newActivity, oldActivity)

        // THEN
        // Comprobamos que se guarda la actividad con los nuevos datos
        coVerify { tripRepository.addActivity(match { it.description == "Templo Renovado" && it.costEstimate == newCost }) }

        // El coste original (1500L) + la diferencia (+70L) = 1570L
        coVerify { tripRepository.editTrip(match { it.totalCost == 1570L }) }
    }
}