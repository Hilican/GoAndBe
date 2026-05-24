package com.github.hilican.goandbe.ui.screens.Components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.data.Room.TripWithItinerary
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.ui.screens.VerticalGap
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchDialog(
    trips: List<TripWithDetails>,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    onConfirmSearch: (tripId: Int, startMillis: Long, endMillis: Long, selectedCity: String) -> Unit
) {
    val cities = listOf("Barcelona", "Paris", "Londres")
    var doe by remember { mutableLongStateOf(0) }
    var dod by remember { mutableLongStateOf(0) }

    // Estado para el viaje seleccionado (empezamos con el primero de la lista si existe)
    var selectedTrip by remember { mutableStateOf(trips.firstOrNull()) }
    var isDropdownExpandedTrip by remember { mutableStateOf(false) }
    var isDropdownExpandedCity by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(cities.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val start = doe
                    val end = dod
                    val tripId = selectedTrip?.trip?.tripId // Ajusta según la estructura de tu TripWithItinerary

                    if (start != 0.toLong() && end != 0.toLong() && tripId != null) {
                        onConfirmSearch(tripId, start, end, selectedCity)
                    }
                },
                enabled = doe != 0.toLong() &&
                        dod != 0.toLong() &&
                        selectedTrip != null
            ) {
                if (isLoading) {
                    // Si carga, mostramos un mini spinner circular blanco dentro del botón
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscando...")
                } else {
                    Text("Buscar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) { Text("Cancelar") }
        },
        title = { Text("Configurar Búsqueda", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. Selector de Viaje (DropdownMenu) ---
                Text("Selecciona tu viaje:", style = MaterialTheme.typography.bodyMedium)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { if (!isLoading) isDropdownExpandedTrip = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(selectedTrip?.trip?.name ?: "No tienes viajes")
                    }
                    DropdownMenu(
                        expanded = isDropdownExpandedTrip,
                        onDismissRequest = { isDropdownExpandedTrip = false }
                    ) {
                        trips.forEach { tripWithItinerary ->
                            DropdownMenuItem(
                                text = { Text(tripWithItinerary.trip.name) },
                                onClick = {
                                    selectedTrip = tripWithItinerary
                                    isDropdownExpandedTrip = false
                                }
                            )
                        }
                    }
                }
                VerticalGap()
                /* ───── Selector de ciudad ───── */
                Text("Selecciona la ciudad:", style = MaterialTheme.typography.bodyMedium)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { if (!isLoading) isDropdownExpandedCity = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(text = selectedCity)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpandedCity,
                        onDismissRequest = { isDropdownExpandedCity = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    isDropdownExpandedCity = false
                                }
                            )
                        }
                    }
                }
                VerticalGap()

                // --- 2. Selector de Rango de Fechas ---
                Text("Selecciona las fechas:", style = MaterialTheme.typography.bodyMedium)
                Column(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp
                )) {
                    DatePickerField(
                        selectedDate = doe,
                        label = "Entry Date",
                        onDateSelected = { doe = it},
                        mode = DatePickerMode.TRIP,
                        enabled = !isLoading,
                    )

                    DatePickerField(
                        selectedDate = dod,
                        label = "Departure Date",
                        onDateSelected = { dod = it},
                        mode = DatePickerMode.TRIP,
                        enabled = !isLoading,
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            HotelSearchDialog(
                trips = emptyList(),
                onDismiss = { },
                isLoading = false,
                onConfirmSearch = { tripId, start, end, city -> }
            )
        }
    }
}