package com.github.hilican.goandbe.ui.screens.Components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.hilican.goandbe.data.Room.Trip
import com.github.hilican.goandbe.data.Room.TripWithDetails
import com.github.hilican.goandbe.data.Room.TripWithItinerary
import com.github.hilican.goandbe.domain.TripMocks
import com.github.hilican.goandbe.ui.screens.VerticalGap
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TripItem(
    tripWithItinerary: TripWithDetails,
    hasReservations: Boolean,
    onDeleteClick: (Trip) -> Unit = {},
    onAddActivityConfirm: (String, Long, Long) -> Unit = { _, _, _ -> },
    onViewActivitiesClick: (Int) -> Unit = {},
    onAddImageClick: (Int, Uri) -> Unit = { _, _ -> },
    onDeleteImageClick: (Int, String) -> Unit = { _, _ -> }
) {
    val trip = tripWithItinerary.trip // Acceso directo a los datos del viaje
    var showOptions by remember { mutableStateOf(false) }

    var showGallery by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false)}
    var selectedTripId by remember { mutableStateOf<Int?>(null) }

    val startDate = remember(trip.startDate) { trip.startDate.toUtcDateString("dd/MM/yyyy") }
    val endDate = remember(trip.endDate) { trip.endDate.toUtcDateString("dd/MM/yyyy") }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Si el usuario seleccionó una imagen (uri no es null), se la mandamos a la pantalla superior
        if (uri != null) {
            onAddImageClick(trip.tripId, uri)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        // 3. Al hacer clic, alternamos entre expandido y contraído
        onClick = { showOptions = !showOptions }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f, fill = false) // Evita que textos muy largos pisen el icono
                )
                if (hasReservations) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Tiene hospedaje reservado",
                        tint = MaterialTheme.colorScheme.secondary, // Puedes usar el secundario para que resalte más
                        modifier = Modifier
                            .size(26.dp) // 🌟 Aumentamos de 20.dp a 26.dp para que tenga más presencia al lado del título
                    )
                }
            }

            VerticalGap(4)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${startDate} - ${endDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            VerticalGap(8)

            // Visualización del Coste Actual (con clic para editar)
            Text(
                text = "Coste total: ${trip.totalCost}€",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Menú expandible con los 3 botones
            if (showOptions) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón para ver actividades
                    Button(
                        onClick = { onViewActivitiesClick(trip.tripId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver itinerario (${tripWithItinerary.activities.size})")
                    }

                    // Botón para añadir actividad
                    FilledTonalButton(
                        onClick = {
                            selectedTripId = trip.tripId // Guardamos a qué viaje le vamos a añadir la actividad
                            showAddActivityDialog = true // Cambiamos el estado para mostrar el diálogo
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Añadir actividad")
                    }

                    // Ver galeria
                    val fotosCount = trip.galleryImageUris.size
                    OutlinedButton(
                        onClick = { showGallery = !showGallery },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showGallery) "Ocultar galería" else "Ver galería ($fotosCount)")
                    }

                    // Añadir foto a la galería
                    FilledTonalButton(
                        onClick = {
                            // Lanzamos el selector pidiendo solo imágenes
                            pickMediaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Añadir foto a la galería")
                    }

                    // Botón para eliminar (en rojo para alertar al usuario)
                    OutlinedButton(
                        onClick = { onDeleteClick(trip) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar viaje")
                    }
                }
            }

            if (showAddActivityDialog && selectedTripId != null) {
                AddActivityDialog(
                    tripStartDate = trip.startDate,
                    tripEndDate = trip.endDate,
                    onDismiss = {
                        showAddActivityDialog = false
                        selectedTripId = null
                    },
                    onConfirm = { desc, dateMillis, costInt ->
                        onAddActivityConfirm(desc, dateMillis, costInt)

                        showAddActivityDialog = false
                        selectedTripId = null
                    }
                )
            }

            // 2. SECCIÓN DE LA GALERÍA
            if (showGallery) {
                Spacer(modifier = Modifier.height(16.dp))

                if (trip.galleryImageUris.isEmpty()) {
                    Text(
                        text = "No hay fotos en este viaje todavía.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // Invocamos tu carrusel reutilizable pasándole las URIs locales
                    RoomImageCarousel(
                        images = trip.galleryImageUris,
                        onDeleteImageClick = { imagePath ->
                            // Le pasamos a la pantalla superior el ID de este viaje y la ruta de la foto exacta
                            onDeleteImageClick(trip.tripId, imagePath)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AddActivityDialog(
    tripStartDate: Long,
    tripEndDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit
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
                // Campo 1 Descripción
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

                // Campo 2 Fecha
                DatePickerField(
                    label = "Día de la actividad",
                    selectedDate = dateMillis,
                    onDateSelected = {
                        dateMillis = it
                        dateErrorMessage = null
                    },
                    isError = dateErrorMessage != null,
                )

                if (dateErrorMessage != null) {
                    Text(
                        text = dateErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Campo 3 (Coste)
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
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }

                    val startDateStr = formatter.format(tripStartDate)
                    val endDateStr = formatter.format(tripEndDate)

                    // Validación
                    val costInt = costText.toLongOrNull()

                    val descError = description.isBlank()
                    var dErrorMsg: String? = null
                    if (dateMillis == 0L) {
                        dErrorMsg = "La fecha es obligatoria"
                    } else if (dateMillis < tripStartDate || dateMillis > tripEndDate) {
                        // Si se sale del rango, le mostramos exactamente entre qué días debe elegir
                        dErrorMsg = "Debe ser entre $startDateStr y $endDateStr"
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

@Preview(showBackground = true, name = "Trip Item - Cerrado")
@Composable
private fun TripItemClosedPreview() {
    GoAndBeTheme {
        // Usamos el mockTrip que definimos antes
        TripItem(
            tripWithItinerary = TripMocks.mockTripWithDetails,
            onDeleteClick = {},
            onAddActivityConfirm = { _, _, _ -> },
            onViewActivitiesClick = {},
            onAddImageClick = {_, _ -> },
            hasReservations = true,
        )
    }
}

fun Long.toUtcDateString(pattern: String = "dd/MM/yyyy"): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(this))
}