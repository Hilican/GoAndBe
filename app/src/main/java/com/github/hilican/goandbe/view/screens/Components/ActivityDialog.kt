package com.github.hilican.goandbe.view.screens.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.hilican.goandbe.data.Room.ItineraryItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ActivityDialog(
    tripStartDate: Long,
    tripEndDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit,
    activityToEdit: ItineraryItem? = null,
) {
    // 1. Estados locales para los campos
    var description by remember { mutableStateOf(activityToEdit?.description ?: "") }
    var dateMillis by remember { mutableLongStateOf(activityToEdit?.activityTime ?: 0L) }
    var costText by remember { mutableStateOf(activityToEdit?.costEstimate?.toString() ?: "") }

    // Estados para manejar errores visuales (bordes rojos)
    var isDescriptionError by remember { mutableStateOf(false) }
    var dateErrorMessage by remember { mutableStateOf<String?>(null) }
    var isCostError by remember { mutableStateOf(false) }

    val dialogTitle = if (activityToEdit == null) "Nueva Actividad" else "Editar Actividad"
    val buttonText = if (activityToEdit == null) "Añadir" else "Guardar"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = dialogTitle, style = MaterialTheme.typography.titleLarge)
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
                Text(buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}