package com.github.hilican.goandbe.ui.screens.TripListScreenExtras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.hilican.goandbe.domain.Trip
import com.github.hilican.goandbe.domain.mockTrip
import com.github.hilican.goandbe.ui.screens.AddActivityDialog
import com.github.hilican.goandbe.ui.screens.VerticalGap
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme

@Composable
fun TripItem(
    trip: Trip,
    onDeleteClick: (Int) -> Unit = {},
    onAddActivityConfirm: (String, Long, Int) -> Unit = { _, _, _ -> },
    onViewActivitiesClick: (Int) -> Unit = {}
) {
    var showOptions by remember { mutableStateOf(false) }

    var showAddActivityDialog by remember { mutableStateOf(false)}
    var selectedTripId by remember { mutableStateOf<Int?>(null) }

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
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

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
                    text = "${trip.startDate} - ${trip.endDate}",
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
                        onClick = { onViewActivitiesClick(trip.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver itinerario (${trip.activities.size})")
                    }

                    // Botón para añadir actividad
                    FilledTonalButton(
                        onClick = {
                            selectedTripId = trip.id // Guardamos a qué viaje le vamos a añadir la actividad
                            showAddActivityDialog = true // Cambiamos el estado para mostrar el diálogo
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Añadir actividad")
                    }

                    // Botón para eliminar (en rojo para alertar al usuario)
                    OutlinedButton(
                        onClick = { onDeleteClick(trip.id) },
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
        }
    }
}

@Preview(showBackground = true, name = "Trip Item - Cerrado")
@Composable
private fun TripItemClosedPreview() {
    GoAndBeTheme {
        // Usamos el mockTrip que definimos antes
        TripItem(
            trip = mockTrip,
            onDeleteClick = {},
            onAddActivityConfirm = { _, _, _ -> },
            onViewActivitiesClick = {}
        )
    }
}