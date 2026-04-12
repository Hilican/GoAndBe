package com.github.hilican.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.screens.*
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel

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
    val sharedViewModel: TripListViewModel = viewModel()
    NavGraph(navController = navController, sharedViewModel = sharedViewModel)
}