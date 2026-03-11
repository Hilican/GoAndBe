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
                toAboutUs = { navController.navigate(AboutUsRoute) },
                toLogIn = { navController.navigate(LogInRoute) },
                toPreferences = { navController.navigate(PreferencesRoute) },
                toSignIn = { navController.navigate(SignInRoute) },
                toTermsAndConditions = { navController.navigate(TermsAndConditionsRoute) },
                toTripList = { navController.navigate(TripListScreenRoute) },
            )
        }
        composable<AboutUsRoute> {
            AboutUsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<PreferencesRoute> {
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<SignInRoute> {
            SignInScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<TermsAndConditionsRoute> {
            TermsAndConditionsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<TripListScreenRoute> {
            TripListScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}