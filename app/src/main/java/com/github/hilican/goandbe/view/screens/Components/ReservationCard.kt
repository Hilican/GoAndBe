package com.github.hilican.goandbe.view.screens.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.R
import com.github.hilican.goandbe.data.Room.ReservationRoom
import com.github.hilican.goandbe.domain.HotelMock.mockReservationRoom
import com.github.hilican.goandbe.domain.calculateTotalCost
import com.github.hilican.goandbe.view.theme.GoAndBeTheme

@Composable
fun ReservationCard(
    item: ReservationRoom,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalCost = calculateTotalCost(startDate = item.startDate, endDate = item.endDate, pricePerNight = item.room.price)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {

            /* ---------- thumbnail ---------- */
            Image(
                painter = rememberAsyncImagePainter(
                    model = BuildConfig.HOTELS_API_URL + item.room.images.firstOrNull(),
                    placeholder = painterResource( R.drawable.ic_launcher_foreground)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            )

            /* ---------- info ---------- */
            Column(
                Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.room.roomType.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${item.startDate} → ${item.endDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "€${totalCost}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /* ---------- delete ---------- */
            FilledIconButton(
                onClick = {onDelete(item.id)},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .align(Alignment.CenterVertically) // 🎯 Centrado perfecto de altura
                    .shadow(4.dp, CircleShape) // 🌟 Le da relieve y profundidad
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar reserva"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        ReservationCard(
            item = mockReservationRoom,
            onDelete = {  },
            modifier = Modifier.padding(8.dp)
        )
    }
}