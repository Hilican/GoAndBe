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
import androidx.compose.ui.tooling.preview.Preview
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel
import com.github.hilican.goandbe.domain.Trip
import com.github.hilican.goandbe.domain.mockTrip
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import kotlin.Int

@Composable
fun ItineraryScreen(
    tripId: Int,
    viewModel: TripListViewModel,
    onBack: () -> Unit
) {
    val tripList by viewModel.tripList.collectAsState()
    val trip = tripList.find { it.id == tripId }

    // Llamamos al contenido puro
    ItineraryContent(
        trip = trip,
        onBack = onBack,
        onDeleteActivity = { activityId ->
            viewModel.deleteActivityFromTrip(tripId = tripId, activityId = activityId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryContent(
    trip: Trip?, // Recibe el viaje ya buscado o null si está cargando
    onBack: () -> Unit,
    onDeleteActivity: (Int) -> Unit // Callback para borrar
) {
    // Si el viaje es null, mostramos la carga
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

    // Si el viaje existe, mostramos el Scaffold real
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // ... Todo tu código de LazyColumn y trip.activities
        // IMPORTANTE: Donde antes llamabas a viewModel.deleteActivity...
        // ahora llamas a onDeleteActivity(activity.id)
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
                            onDeleteActivity(activity.id)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        // Creamos un objeto de prueba
        ItineraryContent(
            trip = mockTrip,
            onBack = { },
            onDeleteActivity = { }
        )
    }
}