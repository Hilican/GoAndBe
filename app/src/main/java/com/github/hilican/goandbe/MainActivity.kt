package com.github.hilican.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme

import com.github.hilican.goandbe.ui.screens.ToAllScreens
import com.github.hilican.goandbe.ui.screens.AboutUsScreen
import com.github.hilican.goandbe.ui.screens.*

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

    // This is the "Engine" that handles the switching
    NavHost(
        navController = navController,
        startDestination = TempHome // The app starts here
    ) {// 1. ToAllScreens (Starting Screen)
        composable<TempHome> {
            ToAllScreens(
                onAddClick7 = { navController.navigate(AboutUsRoute) }
            )
        }

        // 2. AboutUsScreen
        composable<AboutUsRoute> {
            AboutUsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}