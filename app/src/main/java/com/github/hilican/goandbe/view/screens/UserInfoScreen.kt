package com.github.hilican.goandbe.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.hilican.goandbe.data.Room.UserRoom
import com.github.hilican.goandbe.domain.UserMock
import com.github.hilican.goandbe.view.screens.Components.DatePickerField
import com.github.hilican.goandbe.view.screens.Components.DatePickerMode
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.AuthViewModel


@Composable
fun UserInfoScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            Toast.makeText(context, "Sin sesión activa", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            if (uiState.userRoom != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                viewModel.clearError() // Limpiamos el error para que no se repita el Toast al recomponer
            }
        }
    }

    val currentUser = uiState.userRoom

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if (uiState.isLoading) {
            // 1. MIENTRAS CARGA: Mostramos el indicador gigante
            CircularProgressIndicator()

        } else if (currentUser != null) {
            // 2. ÉXITO: Tenemos los datos del usuario, pintamos el formulario
            UserInfoContent(
                userRoom = currentUser,
                isLoading = uiState.isSaving, // 👈 Pasamos el estado de guardado para congelar botones si hace falta
                onBack = onBack,
                onConfirm = { updatedUser ->
                    viewModel.updateUser(updatedUser)
                    // Nota: El onBack() es mejor manejarlo reactivamente cuando el guardado termine con éxito,
                    // pero si te funcionaba bien aquí, puedes dejarlo o controlarlo con otro LaunchedEffect.
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                onValueChange = { viewModel.clearError() },
            )
        } else {
            // 3. ERROR CRÍTICO: No está cargando y el usuario es nulo (falló la carga inicial)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = uiState.errorMessage ?: "No se pudieron cargar los datos del usuario")
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = { viewModel.checkSessionAndLoadUser() }) {
                        Text("Reintentar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onNavigateToHome() }) {
                        Text("Volver al inicio")
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoContent(
    userRoom: UserRoom,
    isLoading: Boolean, // 👈 Usaremos esta variable en toda la UI
    onBack: () -> Unit,
    onConfirm: (UserRoom) -> Unit,
    onValueChange: () -> Unit
) {
    val scrollState = rememberScrollState()

    var username by remember(userRoom.username) { mutableStateOf(userRoom.username) }
    var dateOfBirth by remember(userRoom.dateOfBirth) { mutableLongStateOf(userRoom.dateOfBirth) }
    var email by remember(userRoom.email) { mutableStateOf(userRoom.email) }

    var phoneNumber by remember(userRoom.phoneNumber) { mutableStateOf(userRoom.phoneNumber ?: "") }
    var receiveEmails by remember(userRoom.receiveEmails) {
        mutableStateOf(
            userRoom.receiveEmails ?: false
        )
    }

    var street by remember(userRoom.address.street) { mutableStateOf(userRoom.address.street ?: "") }
    var city by remember(userRoom.address.city) { mutableStateOf(userRoom.address.city ?: "") }
    var state by remember(userRoom.address.state) { mutableStateOf(userRoom.address.state ?: "") }
    var zipCode by remember(userRoom.address.zipCode) { mutableStateOf(userRoom.address.zipCode ?: "") }
    var country by remember(userRoom.address.country) { mutableStateOf(userRoom.address.country ?: "") }
    var additionalInfo by remember(userRoom.address.additionalInfo) {
        mutableStateOf(
            userRoom.address.additionalInfo ?: ""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "User Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Campo de Username ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading // 👈 Se desactiva si está cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo de Date of Birth ---
        DatePickerField(
            label = "Date of Birth",
            selectedDate = dateOfBirth,
            onDateSelected = { dateOfBirth = it },
            isError = false,
            mode = DatePickerMode.TRIP,
            enabled = !isLoading // 👈 Se desactiva si está cargando (si tu DatePickerField lo soporta)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo de Email (Solo lectura) ---
        OutlinedTextField(
            value = email,
            onValueChange = { },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = true,
            enabled = !isLoading, // 👈 Bloqueado completamente si está cargando
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()
        VerticalGap(24)

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                onValueChange()
            },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading // 👈 Se desactiva si está cargando
        )

        VerticalGap()

        OutlinedTextField(
            value = street,
            onValueChange = {
                street = it
                onValueChange()
            },
            label = { Text("Calle y número") },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading // 👈 Se desactiva si está cargando
        )

        VerticalGap()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                    onValueChange()
                },
                label = { Text("Ciudad") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isLoading // 👈 Se desactiva si está cargando
            )
            OutlinedTextField(
                value = zipCode,
                onValueChange = {
                    zipCode = it
                    onValueChange()
                },
                label = { Text("C.P.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f),
                singleLine = true,
                enabled = !isLoading // 👈 Se desactiva si está cargando
            )
        }

        VerticalGap()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("Provincia") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isLoading // 👈 Se desactiva si está cargando
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("País") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isLoading // 👈 Se desactiva si está cargando
            )
        }

        VerticalGap()

        OutlinedTextField(
            value = additionalInfo,
            onValueChange = {
                additionalInfo = it
                onValueChange()
            },
            label = { Text("Información adicional (Piso, puerta, etc. - Opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading // 👈 Se desactiva si está cargando
        )

        VerticalGap()

        // CHECKBOX DE EMAILS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = receiveEmails,
                onCheckedChange = { receiveEmails = it },
                enabled = !isLoading // 👈 Se desactiva si está cargando
            )
            Text(text = "Deseo recibir correos promocionales")
        }

        VerticalGap()

        // --- Botón de Guardar Cambios ---
        Button(
            onClick = {
                val updateAddress = userRoom.address.copy(
                    street = street,
                    city = city,
                    state = state,
                    zipCode = zipCode,
                    country = country,
                    additionalInfo = additionalInfo
                )
                val updatedUser = userRoom.copy(
                    username = username,
                    dateOfBirth = dateOfBirth,
                    phoneNumber = phoneNumber,
                    receiveEmails = receiveEmails,
                    address = updateAddress,
                )
                onConfirm(updatedUser)
            },
            enabled = !isLoading, // 👈 Crucial: Bloquea el botón mientras guarda
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                // 🔄 Si está guardando, muestra una ruedita pequeña blanca en vez de texto
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Confirm Changes", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón de Volver ---
        Button(
            onClick = { onBack() },
            enabled = !isLoading, // 👈 Evita que el usuario regrese atrás a mitad de un guardado
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Return", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview() {
    GoAndBeTheme {
        // Creamos un objeto de prueba
        val context = LocalContext.current

        UserInfoContent(
            userRoom = UserMock.newEmptyUserRoom,
            isLoading = false,
            onBack = { },
            onConfirm = { _ ->
                {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                }
            },
            onValueChange = { }
        )
    }
}