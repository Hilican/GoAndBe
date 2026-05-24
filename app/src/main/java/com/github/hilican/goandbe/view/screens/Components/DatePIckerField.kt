package com.github.hilican.goandbe.view.screens.Components

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DatePickerMode {
    BIRTHDAY, // Hoy o antes
    TRIP,     // Hoy o después
    ALL       // Selector normal (Cualquier fecha)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    mode: DatePickerMode = DatePickerMode.ALL
) {
    var showModal by remember { mutableStateOf(false) }
    val calendar = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    val today = calendar.timeInMillis
    // 2. 🌟 ¡LA SOLUCIÓN! Blindamos el SelectableDates dentro de un remember protegido para Previews
    val selectableDatesRules = remember(mode) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return when (mode) {
                    DatePickerMode.BIRTHDAY -> utcTimeMillis <= today
                    DatePickerMode.TRIP -> utcTimeMillis >= today
                    DatePickerMode.ALL -> true
                }
            }

            override fun isSelectableYear(year: Int): Boolean {
                return when (mode) {
                    DatePickerMode.BIRTHDAY -> year <= calendar.get(Calendar.YEAR)
                    DatePickerMode.TRIP -> year >= calendar.get(Calendar.YEAR)
                    DatePickerMode.ALL -> true
                }
            }
        }
    }

    // 3. Le pasamos las reglas ya memorizadas al estado del DatePicker
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDatesRules
    )

    val dateDisplayString = remember(selectedDate) {
        if (selectedDate > 0L) { // Simplificado: Long primitivo no es nulo
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            formatter.format(Date(selectedDate))
        } else {
            ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showModal = true }
    ) {
        OutlinedTextField(
            value = dateDisplayString,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            shape = RectangleShape,
            enabled = false, // Se mantiene false para que no parpadee el cursor ni el teclado

            // 👈 3. Ajustamos los colores dinámicamente según si está 'enabled' o en 'error'
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Color apagado
                    else -> MaterialTheme.colorScheme.onSurface
                },
                disabledLabelColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                disabledBorderColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) // Borde muy sutil
                    else -> MaterialTheme.colorScheme.outline
                },
                disabledTrailingIconColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            isError = isError
        )
    }

    // 2. The Experimental Dialog
    if (showModal) {
        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(onClick = {
                    val dateMillis = datePickerState.selectedDateMillis
                    if (dateMillis != null) {
                        onDateSelected(dateMillis)
                    }
                    showModal = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        // Creamos un objeto de prueba
        DatePickerField(
            label = "Test",
            selectedDate = 0L,
            onDateSelected = { },
        )
    }
}