package com.github.hilican.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.github.hilican.goandbe.data.AppDatabase
import com.github.hilican.goandbe.domain.TripRepository
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme
import com.github.hilican.goandbe.ui.screens.*
import com.github.hilican.goandbe.ui.viewmodels.AppViewModelProvider
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Creamos la instancia de la base de datos
        // "go_and_be_database" es el nombre del archivo real en el móvil
        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "go_and_be_database"
            ).fallbackToDestructiveMigration(false) // <--- AÑADE ESTA LÍNEA
        .build()

        setContent {
            GoAndBeTheme {
                TravelerAppNavigation(db)
            }
        }
    }
}

@Composable
fun TravelerAppNavigation(db: AppDatabase) {
    val navController = rememberNavController()

    val factory = AppViewModelProvider.factory(db.userDao(), TripRepository(db.tripDao()))

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val tripListViewModel: TripListViewModel = viewModel(factory = factory)

    NavGraph(
        navController = navController,
        authViewModel = authViewModel,
        tripListViewModel = tripListViewModel
    )
}