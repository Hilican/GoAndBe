package com.github.hilican.goandbe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.hilican.goandbe.ui.screens.TripListScreenExtras.DatePickerField
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel


@Composable
fun UserInfoScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.clearError()
        viewModel.fetchUserData()
    }

    val userData = viewModel.currentUserData
    val isLoading = viewModel.isLoading
    val context = LocalContext.current

    if (isLoading && userData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        UserInfoContent(
            initialUsername = userData?.username ?: "",
            initialDob = userData?.dateOfBirth ?: 0L,
            initialEmail = userData?.email ?: "",
            isLoading = viewModel.isLoading,
            onBack = onBack,
            onConfirm = { newName, newDob ->
                viewModel.updateProfile(newName, newDob) {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    onBack() // Volvemos atrás tras guardar
                }
            }
        )
    }
}

@Composable
fun UserInfoContent(
    initialUsername: String,
    initialDob: Long,
    initialEmail: String,
    isLoading: Boolean,
    onBack: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    // 1. State for the input fields
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var dateOfBirth by remember(initialDob) { mutableLongStateOf(initialDob) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo de Date of Birth ---
        DatePickerField(
            label = "Date of Birth",
            selectedDate = dateOfBirth,
            onDateSelected = { dateOfBirth = it },
            isError = false,
            pastDatesAllowed = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Campo de Email (Solo lectura) ---
        OutlinedTextField(
            value = initialEmail,
            onValueChange = { },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            readOnly = true,
            enabled = true, // Opcional: Lo oscurece un poco para que quede claro que no es editable.
            colors = OutlinedTextFieldDefaults.colors(
            // Texto siempre el color principal del tema
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

            // Borde siempre gris
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color.LightGray,

            // Label (el título "Email")
            focusedLabelColor =  MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor =  MaterialTheme.colorScheme.onSurface
        )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón de Guardar Cambios ---
        Button(
            onClick = {
                onConfirm(username, dateOfBirth)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirm Changes", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón de Volver ---
        Button(
            onClick = {
                onBack()
            },
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
            initialUsername = "Paco",
            initialDob = 3490875L,
            initialEmail = "test@gmail.com",
            isLoading = false,
            onBack = { },
            onConfirm = { newName, newDob ->
                {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}