package com.github.hilican.goandbe.ui.screens.Components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.hilican.goandbe.domain.HotelMock.mockImages

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomImageCarousel(
    images: List<String>,
    onDeleteImageClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return // Nada que mostrar

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f) // Proporción estética de aspecto 16:10
        ) { page ->
            val imagePath = images[page]
            Box(modifier = Modifier.fillMaxSize()) {

                // 1. Primer hijo del Box: La imagen de fondo
                AsyncImage(
                    model = imagePath,
                    contentDescription = "Imagen del viaje",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Segundo hijo del Box: El botón flotante encima de la imagen
                IconButton(
                    onClick = { onDeleteImageClick(imagePath) },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // 🌟 Aquí YA NO dará error, porque está DENTRO del Box
                        .padding(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar esta foto"
                    )
                }

            }
        }

        /* Mini indicador de páginas */
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(images.size) { index ->
                val selected = pagerState.currentPage == index
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(8.dp)
                        .padding(horizontal = 2.dp)
                ) {}
            }
        }
    }
}

@Preview
@Composable
private fun preview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            RoomImageCarousel(
                images = mockImages,
                onDeleteImageClick = {_ ->},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}