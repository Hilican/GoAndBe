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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    viewModel: AuthViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MainPageContent(
        modifier = Modifier,
        toAboutUs = toAboutUs,
        toLogIn = toLogIn,
        toPreferences = toPreferences,
        toSignIn = toSignIn,
        toTermsAndConditions = toTermsAndConditions,
        toTripList = toTripList,
        toUserSettings = toUserSettings,
        onLogOutClick = { ->
            viewModel.logOut()
        },
        isLoggedIn = state.isAuthenticated
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageContent(
    modifier: Modifier = Modifier,
    toAboutUs: () -> Unit,
    toLogIn: () -> Unit,
    toPreferences: () -> Unit,
    toSignIn: () -> Unit,
    toTermsAndConditions: () -> Unit,
    toTripList: () -> Unit,
    toUserSettings: () -> Unit,
    onLogOutClick:  () -> Unit,
    isLoggedIn: Boolean
    ) {
    //val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    val forAllOptions = rememberDrawerState(initialValue = DrawerValue.Closed)
    val userOptions = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 2. El contenedor principal que permite el deslizamiento lateral
    ModalNavigationDrawer(
        drawerState = userOptions,
        drawerContent = {
            // contenido que aparece dentro del menú
            ModalDrawerSheet {
                if(!isLoggedIn)
                {
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
                }else
                {
                    Text("Opciones de usuario", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Ajustes de usuario") },
                        selected = false,
                        onClick = toUserSettings,
                    )
                    NavigationDrawerItem(
                        label = { Text("Lista de viajes") },
                        selected = false,
                        onClick = toTripList,
                    )
                    NavigationDrawerItem(
                        label = { Text("Cerrar Session") },
                        selected = false,
                        onClick = {
                            onLogOutClick()
                            coroutineScope.launch {
                                userOptions.close()
                            }
                        },
                    )
                }
            }
        },
    ){
        ModalNavigationDrawer(
            drawerState = forAllOptions,
            drawerContent = {
                // Este es el contenido que aparece DENTRO del menú
                ModalDrawerSheet {
                    Text("Menú Principal", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Preferencias") },
                        selected = false,
                        onClick = toPreferences,
                    )
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
                                scope.launch { forAllOptions.open() }
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
            ) { padding ->
                Column(modifier.padding(padding)) {
                    Text(text = "contenido")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preview()
{
    GoAndBeTheme {
        MainPageContent(
            toAboutUs = {},
            toLogIn = {},
            toPreferences = {},
            toSignIn = {},
            toTermsAndConditions = {},
            toTripList = {},
            toUserSettings = {},
            onLogOutClick = {},
            isLoggedIn = false
        )
    }
}