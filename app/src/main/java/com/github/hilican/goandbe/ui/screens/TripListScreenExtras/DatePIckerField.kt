package com.github.hilican.goandbe.ui.screens.TripListScreenExtras

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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    isError: Boolean = true,
    pastDatesAllowed: Boolean = false,
) {
    var showModal by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val today = calendar.timeInMillis
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return if (pastDatesAllowed) {
                    utcTimeMillis <= today // Para cumpleaños: hoy o antes
                } else {
                    utcTimeMillis >= today // Para viajes: hoy o después
                }
            }

            override fun isSelectableYear(year: Int): Boolean {
                // Que no vean años anteriores al actual
                return if (pastDatesAllowed) {
                    year <= calendar.get(Calendar.YEAR)
                } else {
                    year >= calendar.get(Calendar.YEAR)
                }
            }
        }
    )

    val dateDisplayString = remember(selectedDate) {
        if (selectedDate != null && selectedDate > 0L) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC") // Importante para DatePicker
            }
            formatter.format(Date(selectedDate))
        } else {
            "" // Si es null, mostramos el campo vacío
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
            shape = RectangleShape, // Keeping your sharp borders
            enabled = false, // Prevents keyboard from popping up
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = if(isError)  MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledTrailingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
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