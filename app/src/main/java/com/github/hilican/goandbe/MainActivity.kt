package com.github.hilican.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.hilican.goandbe.view.theme.GoAndBeTheme
import com.github.hilican.goandbe.view.screens.*
import com.github.hilican.goandbe.viewmodel.AuthViewModel
import com.github.hilican.goandbe.viewmodel.HotelViewModel
import com.github.hilican.goandbe.viewmodel.TripListViewModel
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