package com.github.hilican.goandbe.ui.screens.TripListScreenExtras

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    tripId: Int,
    viewModel: TripListViewModel,
    onBack: () -> Unit
) {
    // Obtenemos la lista reactiva
    val tripList by viewModel.tripList.collectAsState()

    // Buscamos el viaje específico
    val trip = tripList.find { it.id == tripId }

    // Si por algún motivo se borró el viaje mientras lo veíamos, volvemos atrás
    if (trip == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cargando...") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando detalles del viaje...")
            }
        }
        return // Detenemos la ejecución aquí hasta que el viaje cargue
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerario: ${trip.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (trip.activities.isEmpty()) {
            // Pantalla vacía si no hay actividades
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay actividades planificadas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Lista ordenada por fecha
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Ordenamos las actividades cronológicamente
                items(trip.activities.sortedBy { it.activityTime }) { activity ->
                    ActivityItem(
                        activity = activity,
                        onDeleteClick = {
                            viewModel.deleteActivityFromTrip(tripId = trip.id, activityId = activity.id)
                        }
                    )
                }
            }
        }
    }
}