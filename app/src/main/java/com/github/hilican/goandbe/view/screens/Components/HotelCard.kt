package com.github.hilican.goandbe.view.screens.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.R
import com.github.hilican.goandbe.domain.HotelMock
import com.github.hilican.goandbe.domain.model.Hotel
import com.github.hilican.goandbe.view.theme.GoAndBeTheme


    @Composable
    fun HotelCard(
        hotel: Hotel,
        onClick: () -> Unit,
        isClickable: Boolean,
        modifier: Modifier = Modifier
    ) {
        Card(
            onClick = { if (isClickable) onClick() },
            modifier = modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isClickable) 4.dp else 0.dp // Menos sombra si está desactivada
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(Modifier.height(120.dp)){

                /* ---------- thumbnail ---------- */
                Image(
                    painter = rememberAsyncImagePainter(
                        model = BuildConfig.HOTELS_API_URL + hotel.imageUrl,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                )

                /* ---------- info ---------- */
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = hotel.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = hotel.address, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(), // Importante para que los extremos se separen
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- BLOQUE IZQUIERDO: Habitaciones ---
                        Icon(
                            imageVector = Icons.Default.Home, // Puedes usar Icons.Default.Menu o uno de cama si tienes
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Controlamos si la lista de habitaciones es nula o vacía mostrando 0
                        Text(
                            text = "${hotel.rooms?.size ?: 0} habs",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // --- EL IMÁN: Empuja el siguiente bloque a la derecha ---
                        Spacer(modifier = Modifier.weight(1f))

                        // --- BLOQUE DERECHO: Estrellas / Rating ---
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${hotel.rating}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HotelCard(
                hotel = HotelMock.mockHotel,
                onClick = {},
                isClickable = false,
            )
        }
    }
}