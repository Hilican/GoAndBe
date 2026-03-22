package com.github.hilican.goandbe.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.hilican.goandbe.ui.screens.TripListScreenExtras.DatePickerField

@OptIn(ExperimentalMaterial3Api::class)//ExposedDropdownMenuBox
@Composable
fun UserSettingsScreen(
    onBack: () -> Unit,
) {
    // 1. State for the input fields
    val languages = listOf("English", "Spanish", "French", "Portuguese", "German")
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    }
    val savedUsername = remember {
        sharedPreferences.getString("username", "") ?: String()
    }
    val savedDateOfBirth = remember {
        sharedPreferences.getLong("date_of_birth", 0L)
    }
    val savedLanguage = remember {
        sharedPreferences.getString("language", languages[0]) ?: languages[0]
    }
    val savedDarkMode = remember {
        sharedPreferences.getBoolean("dark_model", true)
    }

    var expanded by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf(savedUsername) }
    var dateOfBirth by remember { mutableLongStateOf(savedDateOfBirth) }
    var selectedLanguage by remember { mutableStateOf(savedLanguage) }
    var darkMode by remember { mutableStateOf(savedDarkMode) }

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

        // --- Fila de Dark Mode ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Dark Mode", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (darkMode) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Switch(
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Dropdown de Idioma ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("Language") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            selectedLanguage = language
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Botón de Guardar Cambios ---
        Button(
            onClick = {
                // ACTUALIZADO: Usamos las mismas claves de arriba y guardamos los estados actuales
                sharedPreferences.edit()
                    .putString("username", username)
                    .putLong("date_of_birth", dateOfBirth)
                    .putString("language", selectedLanguage)
                    .putBoolean("dark_model", darkMode)
                    .apply()

                Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
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