package com.github.hilican.goandbe.view.screens.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.hilican.goandbe.data.Room.ItineraryItem
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.viewmodel.TripListViewModel
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.HotelViewModel
import kotlin.Int

@Composable
fun ItineraryScreen(
    tripId: Int,
    tripViewModel: TripListViewModel,
    hotelViewModel: HotelViewModel,
    onBack: () -> Unit
) {
    val tripList by tripViewModel.tripList.collectAsState()
    val trip = tripList.find { it.trip.tripId == tripId }

    // Llamamos al contenido puro
    ItineraryContent(
        item = trip,
        onBack = onBack,
        onDeleteActivity = { activity ->
            tripViewModel.deleteActivity(
                tripId = tripId,
                activity = activity
            )
        },
        onDeleteReservation = { reserveId ->
            hotelViewModel.cancelReserve(reserveId)
        },
        onUpdateActivity = { newActivity, oldActivity ->
            tripViewModel.updateActivity(newActivity, oldActivity)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryContent(
    item: TripWithDetails?, // Recibe el viaje ya buscado o null si está cargando
    onBack: () -> Unit,
    onDeleteActivity: (ItineraryItem) -> Unit, // Callback para borrar
    onDeleteReservation: (String) -> Unit,
    onUpdateActivity: (ItineraryItem, ItineraryItem) -> Unit = {_, _ -> },
) {
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    // Si el viaje es null, mostramos la carga
    if (item == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cargando...") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando detalles del viaje...")
            }
        }
        return // Detenemos la ejecución aquí hasta que el viaje cargue
    }

    // Si el viaje existe, mostramos el Scaffold real
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.trip.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (item.activities.isEmpty() && item.reservations.isEmpty()) {
            // Pantalla vacía si no hay actividades
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay actividades planificadas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Lista ordenada por fecha
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                // 🏨 SECCIÓN 1: RESERVAS DE HOTELES (Si existen)
                if (item.reservations.isNotEmpty()) {
                    item {
                        Text(
                            text = "Reservas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(items = item.reservations) { reservation ->
                        ReservationCard(
                            item = reservation,
                            DeleteOption = true,
                            onDelete = { onDeleteReservation(reservation.id) } // Pasa el ID al callback nuevo
                        )
                    }
                }

                // 🗺️ SECCIÓN 2: ACTIVIDADES / ITINERARIO (Si existen)
                if (item.activities.isNotEmpty()) {
                    item {
                        Text(
                            text = "Actividades y Rutas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    // Ordenamos las actividades cronológicamente
                    items(items = item.activities.sortedBy { it.activityTime }) { activity ->
                        ActivityItem(
                            activity = activity,
                            onDeleteClick = {
                                onDeleteActivity(activity)
                            },
                            onEditClick = {activityToEdit = activity}
                        )
                    }
                }
            }
        }
    }

    if (activityToEdit != null) {
        // Guardamos la referencia en una variable local para evitar problemas de sincronización (Smart Cast)
        val currentActivity = activityToEdit

        ActivityDialog(
            tripStartDate = item.trip.startDate,
            tripEndDate = item.trip.endDate,
            activityToEdit = currentActivity,
            onDismiss = {
                // Al cancelar, cerramos el diálogo ocultando el estado
                activityToEdit = null
            },
            onConfirm = { newDescription, newDateMillis, newCost ->
                // Creamos la copia con los nuevos datos introducidos por el usuario
                val updatedActivity = currentActivity?.copy(
                    description = newDescription,
                    activityTime = newDateMillis,
                    costEstimate = newCost
                )

                // Enviamos tanto la actividad modificada como la original (por si necesitas comparar costes/fechas)
                onUpdateActivity(updatedActivity!!, activityToEdit!!)

                // Cerramos el diálogo al terminar con éxito
                activityToEdit = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        ItineraryContent(
            item = TripMocks.mockTripWithDetails,
            onBack = { },
            onDeleteActivity = {},
            onDeleteReservation = {},
        )
    }
}