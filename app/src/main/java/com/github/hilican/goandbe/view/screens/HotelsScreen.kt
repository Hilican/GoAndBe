package com.github.hilican.goandbe.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.hilican.goandbe.domain.HotelMock.mockHotelUi
import com.github.hilican.goandbe.view.screens.Components.HotelCard
import com.github.hilican.goandbe.view.screens.Components.HotelSearchDialog
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.HotelUiState
import com.github.hilican.goandbe.viewmodel.HotelViewModel
import com.github.hilican.goandbe.viewmodel.TripListViewModel

@Composable
fun HotelsScreen(
    hotelViewModel: HotelViewModel,
    tipListViewModel: TripListViewModel,
    onBackClick: () -> Unit,
    toHotelRooms: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Recolectamos el estado del ViewModel
    val hotelState by hotelViewModel.uiState.collectAsStateWithLifecycle()
    val tripList by tipListViewModel.tripList.collectAsStateWithLifecycle()
    var selectedHotelIdForDialog by remember { mutableStateOf<String?>(null) }
    var isSearchDialogOpen by remember { mutableStateOf(false) }
    // Carga inicial de hoteles
    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
    }

    // 3. Contenido visual de la pantalla
    HotelContent(
        state = hotelState,
        onBackClick = {
            hotelViewModel.clearAvailableHotels()
            onBackClick()
        },
        onHotelClick = { hotelId ->
            hotelViewModel.setHotelId(hotelId)
            toHotelRooms()
        },
        onSearchClick = {
            hotelViewModel.clearHotelId()
            isSearchDialogOpen = true
        },
        modifier = modifier
    )

    if (isSearchDialogOpen) {
        HotelSearchDialog(
            trips = tripList,
            isLoading = hotelState.isLoading,
            onDismiss = {
                isSearchDialogOpen = false
                hotelViewModel.clearTripId()
            },
            onConfirmSearch = { tripId, startMillis, endMillis, city ->
                isSearchDialogOpen = false

                hotelViewModel.setTripId(tripId)

                // Ejecutamos la búsqueda de disponibilidad con los datos del diálogo
                hotelViewModel.checkAvailability(
                    start = startMillis,
                    end = endMillis,
                    city = city
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelContent(
    state: HotelUiState,
    onBackClick: () -> Unit,
    onHotelClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isSearchDone = state.availableHotels.isNotEmpty()
    val listToRender = if (isSearchDone) state.availableHotels else state.hotels
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Hoteles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // El botón flotante para ir a buscar por fechas específicas
            ExtendedFloatingActionButton(
                onClick = onSearchClick,
                icon = { Icon(Icons.Default.Search, contentDescription = null) },
                text = { Text("Buscar Fechas") }
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
                    text = state.errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (state.hotels.isEmpty()) {
                Text("No hay hoteles disponibles en este momento.")
            } else {
                // Lista optimizada para cargar muchos hoteles (LazyColumn)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = listToRender, key = { hotel -> hotel.id }) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            // 2. Solo pasamos la acción si la búsqueda está hecha
                            onClick = { onHotelClick(hotel.id) },
                            // 3. Le avisamos a la tarjeta si debe estar activa o no
                            isClickable = isSearchDone
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        HotelContent(
            //state = mockHotelUi.copy(hotels = emptyList()),
            state = mockHotelUi,
            onBackClick = {},
            onHotelClick = {},
            onSearchClick = {}
        )
    }
}