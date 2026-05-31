package com.github.hilican.goandbe.view.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.R
import com.github.hilican.goandbe.domain.HotelMock.mockHotelUiWithHotelId
import com.github.hilican.goandbe.domain.model.Room
import com.github.hilican.goandbe.view.screens.Components.RoomCard
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.HotelUiState
import com.github.hilican.goandbe.viewmodel.HotelViewModel

@Composable
fun RoomsScreen(
    viewModel: HotelViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RoomContent(
        state = state,
        onBackClick = {
            onBackClick()
        },
        onReserve = { roomId ->
            viewModel.createReserve(roomId)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomContent(
    state: HotelUiState,
    onBackClick: () -> Unit,
    onReserve: (String) -> Unit,
    showAddButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var roomToReserve by remember { mutableStateOf<Room?>(null) }

    val selectedHotel = remember(state.availableHotels, state.hotelId) {
        state.availableHotels.find { it.id == state.hotelId }
    }

    val totalNights = remember(state.doe, state.dod) {
        if (state.dod > state.doe) {
            ((state.dod - state.doe) / (1000 * 60 * 60 * 24)).toInt()
        } else {
            1 // Por seguridad, si las fechas fallan, calculamos mínimo 1 noche
        }
    }

    val roomsToShow = selectedHotel?.rooms ?: emptyList()

    LaunchedEffect(roomsToShow) {
        Log.d("RoomContent", "Total habitaciones en lista: ${roomsToShow.size}")
        roomsToShow.forEachIndexed { index, room ->
            Log.d("RoomContent", "Posición $index -> ID: '${room.id}' | Tipo: ${room.roomType}")
        }
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedHotel?.name ?: "Habitaciones Disponibles")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (roomsToShow.isEmpty()) {
                Text(
                    text = "No quedan habitaciones libres en este hotel para las fechas seleccionadas.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ){
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = BuildConfig.HOTELS_API_URL + state.hotels.find { it.id == state.hotelId }?.imageUrl,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground)
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = roomsToShow) { room ->
                            RoomCard(
                                item = room,
                                onAdd = {
                                    roomToReserve = room
                                    showConfirmationDialog = true
                                },
                                showAddButton = showAddButton
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirmationDialog) {
        val totalPrice = roomToReserve?.price?.times(totalNights.toFloat())
        AlertDialog(
            onDismissRequest = {
                // Si el usuario toca fuera o cancela, cerramos y limpiamos
                showConfirmationDialog = false
                roomToReserve = null
            },
            title = {
                Text(text = "Confirmar Reserva")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "¡Ya casi está listo!", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🏨 Hotel: ${selectedHotel?.name ?: "Desconocido"}")
                    Text(text = "🛏️ Tipo: ${roomToReserve?.roomType ?: "Estándar"}")
                    Text(text = "💰 Precio por noche: ${roomToReserve?.price ?: 0} €")
                    Text(text = "🌙 Estancia: $totalNights ${if (totalNights == 1) "noche" else "noches"}")
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )
                    Text(
                        text = "Total a pagar: $totalPrice €",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 4. Si confirma, ejecutamos la reserva real con el ID guardado
                        showConfirmationDialog = false
                        roomToReserve?.let { onReserve(it.id) }
                        roomToReserve = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        roomToReserve = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        RoomContent(
            state = mockHotelUiWithHotelId,
            onBackClick = {},
            onReserve = {}
        )
    }
}