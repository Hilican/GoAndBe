package com.github.hilican.goandbe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    toAboutUs: () -> Unit,
    toLogIn: () -> Unit,
    toPreferences: () -> Unit,
    toSignIn: () -> Unit,
    toTermsAndConditions: () -> Unit,
    toTripList: () -> Unit,
    toUserSettings: () -> Unit,
    ) {
    //val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val idkForNow = rememberDrawerState(initialValue = DrawerValue.Closed)
    val userOptions = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 2. El contenedor principal que permite el deslizamiento lateral
    ModalNavigationDrawer(
        drawerState = userOptions,
        drawerContent = {
            // contenido que aparece dentro del menú
            ModalDrawerSheet {
                Text("Opciones sin iniciar session", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Registrar-se") },
                    selected = false,
                    onClick = toSignIn,
                )
                NavigationDrawerItem(
                    label = { Text("Iniciar session") },
                    selected = false,
                    onClick = toLogIn,
                )
                HorizontalDivider()
                Text("Opciones de usuario", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Ajustes de usuario") },
                    selected = false,
                    onClick = toUserSettings,
                )
                NavigationDrawerItem(
                    label = { Text("Preferencias (dudas)") },
                    selected = false,
                    onClick = toPreferences,
                )
                NavigationDrawerItem(
                    label = { Text("Lista de viajes") },
                    selected = false,
                    onClick = toTripList,
                )
            }
        },
    ){
        ModalNavigationDrawer(
            drawerState = idkForNow,
            drawerContent = {
                // Este es el contenido que aparece DENTRO del menú
                ModalDrawerSheet {
                    Text("Menú Principal", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Terminos & Condiciones") },
                        selected = false,
                        onClick = toTermsAndConditions,
                    )
                    NavigationDrawerItem(
                        label = { Text("Sobre Nosotros") },
                        selected = false,
                        onClick = toAboutUs,
                    )
                }
            },
        ) {
            // 3. El Scaffold con el botón de hamburguesa en la TopBar
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Go And Be") },
                        navigationIcon = {
                            IconButton(onClick = {
                                // Abrir el menú de forma asíncrona
                                scope.launch { idkForNow.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menú"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch { userOptions.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Perfil de usuario"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar {
                        Text(text = "Abajo")
                    }
                },
                /**
                snackbarHost = {
                    // This is the "landing pad"
                    SnackbarHost(hostState = snackbarHostState)
                },
                //Ejemplo de un snackbar
                floatingActionButton = {
                FloatingActionButton(onClick = {
                scope.launch {
                snackbarHostState.showSnackbar("To Develop")
                }
                }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Test")
                }
                }**/
            ) { padding ->
                Column(modifier.padding(padding)) {
                    Text(text = "contenido")
                }
            }
        }
    }
}