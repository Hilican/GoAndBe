package com.github.hilican.goandbe.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    goToForgottenPassword: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            Toast.makeText(context, "Sesión ya activa", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }
    }

    // Aquí conectamos el ViewModel con la UI
    LoginContent(
        errorMsg = state.errorMessage,
        isLoading = state.isLoading,
        onLoginClick = { email, pass ->
            viewModel.singIn(email, pass)
        },
        onBack = onBack,
        onValueChange = { viewModel.clearError() },
        goToForgottenPassword = { goToForgottenPassword() }
    )
}

@Composable
fun LoginContent(
    errorMsg: String?,
    isLoading: Boolean,
    onLoginClick: (String, String) -> Unit,
    onBack: () -> Unit,
    onValueChange: () -> Unit = {},
    goToForgottenPassword: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Email Field
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

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                onValueChange()
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Login Button
        Button(
            onClick = { onLoginClick(email, password) },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                // El spinner dentro del botón
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Log In", fontSize = 18.sp)
            }
        }

        //Error
        Box(modifier = Modifier.height(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { goToForgottenPassword() },
            enabled = !isLoading
        ) {
            Text("¿Olvidaste tu contraseña?")
        }
        // Return Button
        Button(
            onClick = {
                onBack()
            },
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
        LoginContent(
            errorMsg = null,
            isLoading = false,
            onLoginClick = { email, pass -> },
            onBack = {},
            onValueChange = {},
            goToForgottenPassword = {}
        )
    }
}