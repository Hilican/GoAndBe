package com.github.hilican.goandbe.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.Locale

data class Trip(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val createdAt: Long = System.currentTimeMillis() // Used for sorting
)

@Composable
fun TripListScreen(
    onAddClick0: () -> Unit,
) {
    // 1. State: The list of trips
    val tripList = remember { mutableStateListOf<Trip>() }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    onAddClick0()
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
                items(tripList.sortedByDescending { it.createdAt }) {
                        trip -> TripItem(trip)
                }
            }
        }

        // 3. The "Create New Trip" Dialog
        if (showDialog) {
            CreateTripDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, start, end ->
                    tripList.add(Trip(tripList.size, name, start, end))
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TripItem(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            VerticalGap(4) // Using your custom spacer!

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${trip.startDate} - ${trip.endDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CreateTripDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    // 1. Local state for the text fields inside the dialog
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Plan New Trip", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Where to?") },
                    modifier = Modifier.fillMaxWidth()
                )

                DatePickerField(
                    label = "Start Date",
                    selectedDate = startDate,
                    onDateSelected = { startDate = it }
                )

                DatePickerField(
                    label = "End Date",
                    selectedDate = endDate,
                    onDateSelected = { endDate = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // 1. Wrap in a Box to intercept clicks
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showModal = true }
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            shape = RectangleShape, // Keeping your sharp borders
            enabled = false, // Prevents keyboard from popping up
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                        // Formatting the date for your trip list
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        onDateSelected(formatter.format(Date(dateMillis)))
                    }
                    showModal = false
                }) {
                    Text("OK")
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