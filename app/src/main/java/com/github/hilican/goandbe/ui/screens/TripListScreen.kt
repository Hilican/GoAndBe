package com.github.hilican.goandbe.ui.screens

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import java.util.Locale

import com.github.hilican.goandbe.ui.screens.TripListScreenExtras.*
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel
import com.github.hilican.goandbe.domain.Trip
import com.github.hilican.goandbe.domain.mockTrip

@Composable
fun TripListScreen(
    onBack: () -> Unit,
    viewModel: TripListViewModel = viewModel()
) {
    val tripList by viewModel.tripList.collectAsState()

    var tripIdForItinerary by remember { mutableStateOf<Int?>(null) }

    if (tripIdForItinerary != null) {
        ItineraryScreen(
            tripId = tripIdForItinerary!!,
            viewModel = viewModel,
            onBack = {
                // Al volver, limpiamos el estado para que se vuelva a mostrar la lista
                tripIdForItinerary = null
            }
        )
    } else {
        TripListContent(
            tripList = tripList,
            onBack = onBack,
            onAddTrip = { name, start, end -> viewModel.addTrip(name, start, end) },
            onDeleteTrip = { id -> viewModel.deleteTrip(id) },
            onAddActivity = { tripId, desc, date, cost ->
                viewModel.addActivityToTrip(tripId, desc, date, cost)
            },
            onSelectTrip = { selectedId -> tripIdForItinerary = selectedId }
        )
    }
}

@Composable
fun TripListContent(
    tripList: List<Trip>,
    onBack: () -> Unit,
    onAddTrip: (String, String, String) -> Unit,
    onDeleteTrip: (Int) -> Unit,
    onAddActivity: (Int, String, Long, Int) -> Unit,
    onSelectTrip: (Int) -> Unit
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

            Text(
                text = "My Trips",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            // 2. The List (Sorted: Newest at the top)
            LazyColumn {
                items(tripList.sortedByDescending { it.createdAt }) { trip ->
                    TripItem(
                        trip = trip,
                        onDeleteClick = { tripId -> onDeleteTrip(tripId) },
                        onAddActivityConfirm = { desc, dateMillis, cost ->
                            onAddActivity(
                                trip.id,
                                desc,
                                dateMillis,
                                cost
                            )
                        },
                        onViewActivitiesClick = { tripId ->
                            onSelectTrip(tripId)
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

                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }

                    onAddTrip(
                        name,
                        formatter.format(Date(start)),
                        formatter.format(Date(end))
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

@Composable
fun AddActivityDialog(
    tripStartDate: String,
    tripEndDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Int) -> Unit
) {
    // 1. Estados locales para los campos
    var description by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(0L) }
    var costText by remember { mutableStateOf("") }

    // Estados para manejar errores visuales (bordes rojos)
    var isDescriptionError by remember { mutableStateOf(false) }
    var dateErrorMessage by remember { mutableStateOf<String?>(null) }
    var isCostError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Nueva Actividad", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Campo 1: String (Descripción)
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) isDescriptionError = false
                    },
                    label = { Text("Descripción (ej. Visita al museo)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDescriptionError,
                    supportingText = {
                        if (isDescriptionError) Text("La descripción es obligatoria", color = MaterialTheme.colorScheme.error)
                    }
                )

                // Campo 2: Fecha (Usando tu DatePickerField personalizado)
                DatePickerField(
                    label = "Día de la actividad",
                    selectedDate = dateMillis,
                    onDateSelected = {
                        dateMillis = it
                        dateErrorMessage = null
                    },
                    isError = dateErrorMessage != null
                )

                if (dateErrorMessage != null) {
                    Text(
                        text = dateErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Campo 3: Int (Coste)
                OutlinedTextField(
                    value = costText,
                    onValueChange = {
                        costText = it
                        isCostError = false
                    },
                    label = { Text("Coste estimado (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isCostError,
                    supportingText = {
                        if (isCostError) Text("Introduce un número válido", color = MaterialTheme.colorScheme.error)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Fechas en millis
                    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }
                    val startMillis = formatter.parse(tripStartDate)?.time ?: 0L
                    val endMillis = formatter.parse(tripEndDate)?.time ?: Long.MAX_VALUE

                    // Validación
                    val costInt = costText.toIntOrNull()

                    val descError = description.isBlank()
                    var dErrorMsg: String? = null
                    if (dateMillis == 0L) {
                        dErrorMsg = "La fecha es obligatoria"
                    } else if (dateMillis < startMillis || dateMillis > endMillis) {
                        // Si se sale del rango, le mostramos exactamente entre qué días debe elegir
                        dErrorMsg = "Debe ser entre $tripStartDate y $tripEndDate"
                    }
                    val cError = costInt == null // Error si está vacío o no es un número

                    // Actualizamos la UI si hay errores
                    isDescriptionError = descError
                    dateErrorMessage = dErrorMsg
                    isCostError = cError

                    // 3. Confirmamos solo si es correcto
                    if (!descError && dErrorMsg == null && !cError) {
                        // El !! es seguro aquí porque cError garantiza que costInt no es null
                        onConfirm(description, dateMillis, costInt)
                    }
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TripListPreview() {
    GoAndBeTheme {
        // Usamos la versión CONTENT (Stateless)
        TripListContent(
            tripList = listOf(
                mockTrip, // El que creamos antes para Roma
                mockTrip.copy(id = 2, name = "París", startDate = "10/06/2026") // Una copia rápida
            ),
            onBack = { },
            onAddTrip = { _, _, _ -> },
            onDeleteTrip = { },
            onAddActivity = { _, _, _, _ -> },
            onSelectTrip = { }
        )
    }
}