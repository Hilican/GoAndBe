package com.github.hilican.goandbe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel

@Composable
fun ForgottenPasswordScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Limpiamos errores al entrar por primera vez
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    // Escuchamos si Firebase ya envió el correo con éxito
    LaunchedEffect(state.isPasswordResetSent) {
        if (state.isPasswordResetSent) {
            Toast.makeText(
                context,
                "Correo de recuperación enviado. Revisa tu bandeja de entrada.",
                Toast.LENGTH_LONG
            ).show()

            // 1. Apagamos el interruptor en el ViewModel
            viewModel.clearPasswordResetSent()
            // 2. Devolvemos al usuario al Login automáticamente
            onBack()
        }
    }

    // Conectamos con la UI limpia
    ForgottenPasswordContent(
        errorMsg = state.errorMessage,
        isLoading = state.isLoading,
        onResetClick = { email ->
            viewModel.sendPasswordReset(email)
        },
        onBack = onBack,
        onValueChange = { viewModel.clearError() }
    )
}


@Composable
fun ForgottenPasswordContent(
    errorMsg: String?,
    isLoading: Boolean,
    onResetClick: (String) -> Unit,
    onBack: () -> Unit,
    onValueChange: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recover Password",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Texto informativo de ayuda al usuario
        Text(
            text = "Enter your email address below and we will send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onValueChange()
            },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Enviar Correo
        Button(
            onClick = { onResetClick(email) },
            enabled = !isLoading && email.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Send Recovery Email", fontSize = 18.sp)
            }
        }

        // Bloque de error idéntico a tu LoginContent
        Box(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Volver
        Button(
            onClick = { onBack() },
            enabled = !isLoading,
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
        ForgottenPasswordContent(
            errorMsg = null,
            isLoading = false,
            onResetClick = {},
            onBack = {}
        )
    }
}