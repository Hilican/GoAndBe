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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.R
import com.github.hilican.goandbe.domain.HotelMock.mockRoom
import com.github.hilican.goandbe.domain.model.Room
import com.github.hilican.goandbe.view.theme.GoAndBeTheme

@Composable
fun RoomCard(
    item: Room,
    onAdd: (String) -> Unit,
    showAddButton: Boolean,
    modifier: Modifier = Modifier,
) {
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
                    model = BuildConfig.HOTELS_API_URL + item.images.firstOrNull(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            )

            /* ---------- info ---------- */
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Habitación ${item.id}", // Queda más descriptivo que el número suelto
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.roomType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Da mejor contraste según guías Material 3
                )

                Spacer(Modifier.weight(1f))

                // Eliminados los Rows innecesarios; la columna ya los posiciona uno abajo del otro
                Text(
                    text = "Precio noche",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "€${item.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            /* ---------- action button (Add) ---------- */
            if(showAddButton){
                FilledIconButton(
                    onClick = {onAdd(item.id)},
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
                        imageVector = Icons.Default.Add,
                        contentDescription = "Eliminar reserva"
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
        RoomCard(
            item = mockRoom,
            onAdd = {  },
            showAddButton = true,
            modifier = Modifier.padding(8.dp)
        )
    }
}