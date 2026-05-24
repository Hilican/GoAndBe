package com.github.hilican.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.screens.*
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import com.github.hilican.goandbe.ui.viewmodels.HotelViewModel
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GoAndBeTheme {
                TravelerAppNavigation()
            }
        }
    }
}

@Composable
fun TravelerAppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = hiltViewModel()
    val tripListViewModel: TripListViewModel = hiltViewModel()
    val hotelViewModel: HotelViewModel = hiltViewModel()

    NavGraph(
        navController = navController,
        authViewModel = authViewModel,
        tripListViewModel = tripListViewModel,
        hotelViewModel = hotelViewModel
    )
}