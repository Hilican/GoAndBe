package com.github.hilican.goandbe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.hilican.goandbe.domain.model.Address
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.ui.screens.Components.DatePickerField
import com.github.hilican.goandbe.ui.screens.Components.DatePickerMode
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel


@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
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
    SignInContent(
        errorMsg = state.errorMessage,
        isLoading = state.isLoading,
        onSignUpClick = { user ->
            viewModel.signUp(user)
        },
        onBack = onBack,
        onValueChange = { viewModel.clearError() },
        samePasswords = { pass1, pass2 ->
            viewModel.samePasswords(pass1, pass2)
        }
    )
}

@Composable
fun SignInContent(
    errorMsg: String?,
    isLoading: Boolean,
    onSignUpClick: (UserRegistrationRequest) -> Unit,
    onBack: () -> Unit,
    onValueChange: () -> Unit,
    samePasswords: (String, String) -> Boolean
) {
    // states for the input fields
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var dob by remember { mutableLongStateOf(0) }
    var phoneNumber by remember { mutableStateOf("") }
    var receiveEmails by remember { mutableStateOf(false) }

    // states for Address
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        VerticalGap(32)

        // Username Field
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                onValueChange()
            },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )

        VerticalGap()

        // Email Field
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

        VerticalGap()

        // password Field
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

        VerticalGap()

        // password2 Field
        OutlinedTextField(
            value = password2,
            onValueChange = {
                password2 = it
                onValueChange()
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        VerticalGap()

        DatePickerField(
            label = "Día de nacimiento",
            selectedDate = dob,
            onDateSelected = {
                dob = it
                onValueChange()
            },
            mode = DatePickerMode.BIRTHDAY
        )

        VerticalGap(24)
        HorizontalDivider() // Separador visual
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // Teclado numérico
            singleLine = true
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
            singleLine = true
        )

        VerticalGap()

        // Fila para agrupar Ciudad y Código Postal y ahorrar espacio
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
                singleLine = true
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
                singleLine = true
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
                singleLine = true
            )
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("País") },
                modifier = Modifier.weight(1f),
                singleLine = true
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
            singleLine = true
        )

        VerticalGap()

        // CHECKBOX DE EMAILS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = receiveEmails,
                onCheckedChange = { receiveEmails = it }
            )
            Text(text = "Deseo recibir correos promocionales")
        }

        VerticalGap()

        // Sign In Button
        Button(
            onClick = {
                if(samePasswords(password2, password)){
                    val userAddress = Address(
                        street = street,
                        city = city,
                        state = state,
                        zipCode = zipCode,
                        country = country,
                        additionalInfo = additionalInfo.takeIf { it.isNotBlank() }
                    )
                    val userInfo = UserRegistrationRequest(
                        username = username,
                        email = email,
                        password = password,
                        dateOfBirth = dob,
                        address = userAddress,
                        phoneNumber = phoneNumber,
                        receiveEmails = receiveEmails
                    )
                    onSignUpClick(userInfo)
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign In", fontSize = 18.sp)
        }
        errorMsg?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        VerticalGap()

        // 5. Return Button
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
private fun preview()
{
    GoAndBeTheme {
        SignInContent(
            isLoading = false,
            errorMsg = "patata",
            onBack = {},
            onSignUpClick = { _ -> },
            onValueChange = { },
            samePasswords = { _, _ -> true }
        )
    }
}