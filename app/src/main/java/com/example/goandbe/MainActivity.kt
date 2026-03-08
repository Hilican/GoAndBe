package com.example.goandbe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.goandbe.ui.theme.GoAndBeTheme
import com.example.goandbe.ui.screens.MainScreen
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goandbe.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoAndBeTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // Pass the navigation command to the screen
            MainScreen(
                onAddClick1 = {
                    navController.navigate("TripList")
                },
                onAddClick2 = {
                    navController.navigate("Preferences")
                },
                onAddClick3 = {
                    navController.navigate("LogIn")
                },
                onAddClick4 = {
                    navController.navigate("SignIn")
                },
                onAddClick6 = {
                    navController.navigate("TermsAndConditions")
                },
                onAddClick7 = {
                    navController.navigate("AboutUs")
                },
            )
        }
        composable("TripList") {
            TripListScreen(
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
        composable("Preferences") {
            PreferencesScreen(
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
        composable("LogIn") {
            LoginScreen(
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
        composable("SignIn") {
            SignInScreen(
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
        composable("TermsAndConditions") {
            TermsScreen (
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
        composable("AboutUs") {
            AboutUsScreen (
                onAddClick0 = {
                    navController.navigate("home")
                },
            )
        }
    }
}