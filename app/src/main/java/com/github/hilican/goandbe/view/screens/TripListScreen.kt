package com.github.hilican.goandbe.view.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.domain.TripMocks

import com.github.hilican.goandbe.view.screens.Components.*
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.HotelViewModel
import com.github.hilican.goandbe.viewmodel.TripListViewModel

@Composable
fun TripListScreen(
    onBack: () -> Unit,
    tripListViewModel: TripListViewModel,
    hotelViewModel: HotelViewModel
) {
    val tripList by tripListViewModel.tripList.collectAsState()
    val context = LocalContext.current
    var tripIdForItinerary by remember { mutableStateOf<Int?>(null) }

    if (tripIdForItinerary != null) {
        ItineraryScreen(
            tripId = tripIdForItinerary!!,
            onBack = {
                // Al volver, limpiamos el estado para que se vuelva a mostrar la lista
                tripIdForItinerary = null
            },
            tripViewModel = tripListViewModel,
            hotelViewModel = hotelViewModel,
        )
    } else {
        TripListContent(
            tripList = tripList,
            onBack = onBack,
            onAddTrip = { name, start, end ->
                tripListViewModel.addTrip(name, start, end)
            },
            onDeleteTrip = { trip ->
                tripListViewModel.deleteTrip(trip)
            },
            onAddActivity = { tripId, desc, date, cost ->
                tripListViewModel.addActivityToTrip(tripId, desc, date, cost)
            },
            onSelectTrip = { selectedId ->
                // selectedId ahora debe ser String desde TripListContent
                tripIdForItinerary = selectedId
            },
            onAddImageClick = { tripId, uri ->
                tripListViewModel.addImageToTrip(context, tripId, uri)
            },
            onDeleteImageClick = { tripId, imagePath ->
                // Le pasamos a la pantalla superior el ID de este viaje y la ruta de la foto exacta
                tripListViewModel.deleteImage(tripId, imagePath)
            }
        )
    }
}

@Composable
fun TripListContent(
    tripList: List<TripWithDetails>,
    onBack: () -> Unit,
    onAddTrip: (String, Long, Long) -> Unit,
    onDeleteTrip: (Trip) -> Unit,
    onAddActivity: (Int, String, Long, Long) -> Unit,
    onSelectTrip: (Int) -> Unit,
    onAddImageClick: (Int, Uri) -> Unit = { _, _ -> },
    onDeleteImageClick: (Int, String) -> Unit = { _, _ -> }
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

            // --- SECCIÓN MODIFICADA: Título + Leyenda ---
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "My Trips",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp) // Separación con la leyenda
                )

                // Fila para la leyenda explicativa
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Tiene hospedaje reservado",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Con hospedaje reservado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Color sutil para la leyenda
                    )
                }
            }

            // 2. The List (Sorted: Newest at the top)
            LazyColumn {
                items(tripList.sortedByDescending { it.trip.startDate }) { tripWithDetails ->
                    val hasHotels = tripWithDetails.reservations.isNotEmpty()
                    TripItem(
                        hasReservations = hasHotels,
                        tripWithItinerary = tripWithDetails,
                        onDeleteClick = { onDeleteTrip(tripWithDetails.trip) },
                        onAddActivityConfirm = { desc, dateMillis, cost ->
                            onAddActivity(
                                tripWithDetails.trip.tripId,
                                desc,
                                dateMillis,
                                cost
                            )
                        },
                        onViewActivitiesClick = {
                            onSelectTrip(tripWithDetails.trip.tripId)
                        },
                        onAddImageClick = { tripId, uri ->
                            onAddImageClick(tripId, uri)
                        },
                        onDeleteImageClick = { tripId, uri ->
                            onDeleteImageClick(tripId, uri)
                        }
                    )
                }
            }
        }

        // 3. The "Create New Trip" Dialog
        if (showDialog) {
            CreateTripDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, start, end ->
                    onAddTrip(
                        name,
                        start,
                        end
                    )
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateTripDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit
) {
    // 1. Local state for the text fields inside the dialog
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(0L) }
    var endDate by remember { mutableLongStateOf(0L) }

    var isNameError by remember { mutableStateOf(false) }
    var date1ErrorMessage by remember { mutableStateOf<String?>(null) }
    var date2ErrorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Plan New Trip", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) isNameError = false
                    },
                    label = { Text("Trip Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text(text = "El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                DatePickerField(
                    label = "Start Date",
                    selectedDate = startDate,
                    onDateSelected = {
                        startDate = it
                        date1ErrorMessage = null
                    },
                    isError = date1ErrorMessage != null,
                    mode = DatePickerMode.TRIP
                )

                if (date1ErrorMessage != null) {
                    Text(
                        text = date1ErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                DatePickerField(
                    label = "End Date",
                    selectedDate = endDate,
                    onDateSelected = {
                        endDate = it
                        date2ErrorMessage = null
                        },
                    isError = date2ErrorMessage != null,
                    mode = DatePickerMode.TRIP
                )

                if (date2ErrorMessage != null) {
                    Text(
                        text = date2ErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 1. Calculamos qué está mal
                    val nameError = name.isBlank()
                    // Comprobamos que ambas fechas existan Y que el orden sea correcto
                    var d1ErrorMsg: String? = null
                    var d2ErrorMsg: String? = null
                    val dateError = startDate == 0L
                    if(dateError)
                    {
                        d1ErrorMsg = "La fecha es obligatoria"
                    }
                    var date2Error = endDate == 0L
                    if(date2Error)
                    {
                        d2ErrorMsg = "La fecha es obligatoria"
                    }
                    if(!dateError && !date2Error && startDate > endDate)
                    {
                        date2Error = true
                        d2ErrorMsg = "La fecha tiene que ser despues de la primera fecha"
                    }
                    // 2. Actualizamos los estados visuales (los bordes rojos)
                    isNameError = nameError
                    date1ErrorMessage = d1ErrorMsg
                    date2ErrorMessage = d2ErrorMsg

                    // 3. Solo si esta bien, confirmamos
                    if (!nameError && !dateError && !date2Error) {
                        onConfirm(name, startDate, endDate)
                    }
                }
            ) {
                Text("Add Trip")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}


@Preview(showBackground = true)
@Composable
private fun TripListPreview() {
    GoAndBeTheme {
        TripListContent(
            tripList = TripMocks.mockListTripsWithDetails,
            onBack = { },
            onAddTrip = {_,_,_ -> },
            onDeleteTrip = {},
            onAddActivity = {_,_,_,_ -> },
            onSelectTrip = {},
            onAddImageClick = {_ , _ -> }
        )
    }
}