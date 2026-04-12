package com.github.hilican.goandbe.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBack: () -> Unit,
) {
    // 1. State for the input fields
    val languages = listOf("English", "Spanish", "French", "Portuguese", "German")
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    }
    val savedNotifications = remember {
        sharedPreferences.getBoolean("notifications_enabled", true)
    }
    val savedLanguage = remember {
        sharedPreferences.getString("selected_language", languages[0]) ?: languages[0]
    }
    val savedThemes = remember {
        sharedPreferences.getString("saved_themes", "") ?: String()
    }

    var expanded by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(savedNotifications) }
    var selectedLanguage by remember { mutableStateOf(savedLanguage) }
    var theme by remember { mutableStateOf(savedThemes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Preferences",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. The Notification Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Notifications", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (notifications) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // The actual Boolean control
            Switch(
                checked = notifications,
                onCheckedChange = { notifications = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // The "Field" that shows the current choice
            OutlinedTextField(
                value = selectedLanguage,
                onValueChange = {},
                readOnly = true, // Prevents the user from typing
                label = { Text("Language") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable) // Important: aligns the menu to the text field
                    .fillMaxWidth()
            )

            // 3. The actual list of options
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

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Themes Field
        OutlinedTextField(
            value = theme,
            onValueChange = { theme = it },
            label = { Text("Favorite themes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Confirm Changes Button
        Button(
            onClick = {
                // Guardamos el idioma seleccionado de forma persistente
                sharedPreferences.edit()
                    .putString("selected_language", selectedLanguage)
                    .putBoolean("notifications_enabled", notifications)
                    .putString("saved_themes", theme)
                    .apply() // .apply() guarda los datos en segundo plano

                // Opcional: Mostramos un pequeño mensaje para confirmar al usuario
                Toast.makeText(context, "Preferences saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirm Changes", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

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