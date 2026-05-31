package com.github.hilican.goandbe.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.view.screens.Components.ReservationCard
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.TripListViewModel

@Composable
fun AllReservationsScreen(
    onBack: () -> Unit,
    tripListViewModel: TripListViewModel,
){
    val tripList by tripListViewModel.tripList.collectAsState()

    AllReservationsContent(
        tripList = tripList,
        onBack = onBack,
    )
}

@Composable
fun AllReservationsContent(
    tripList: List<TripWithDetails>,
    onBack: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        },
        snackbarHost = {
            // This is the "landing pad"
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text("Return", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "My Reservations",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp) // Separación con la leyenda
                )
            }

            // 2. The List (Sorted: Newest at the top)
            LazyColumn {
                // Ordenamos la lista antes para no penalizar el rendimiento en cada recomposición
                val sortedTrips = tripList.sortedByDescending { it.trip.startDate }

                sortedTrips.forEach { tripWithDetails ->
                    if(tripWithDetails.reservations.isNotEmpty()){
                        // 1. Añadimos el encabezado del viaje como un ítem único
                        item(key = "header_${tripWithDetails.trip.tripId}") {
                            Text(
                                text = "Reserva: ${tripWithDetails.trip.name}",
                                style = MaterialTheme.typography.titleMedium, // O el estilo que prefieras
                                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                            )
                        }

                        // 2. Añadimos cada una de sus reservaciones como ítems individuales
                        items(
                            items = tripWithDetails.reservations,
                            key = { reservation -> reservation.id } // Buenas prácticas para el rendimiento
                        ) { reservation ->
                            ReservationCard(
                                item = reservation
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripListPreview() {
    GoAndBeTheme {
        AllReservationsContent(
            onBack = {},
            tripList = TripMocks.mockListTripsWithDetails,
        )
    }
}