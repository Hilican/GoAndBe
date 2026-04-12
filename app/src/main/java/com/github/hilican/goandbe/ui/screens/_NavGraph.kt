package com.github.hilican.goandbe.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: TripListViewModel
) {
    NavHost(
        navController = navController,
        startDestination = TempHome // The app starts here
    ) {// 1. ToAllScreens (Develpoer Screen)
        composable<TempHome> {
            ToAllScreens(
                toAboutUs = { navController.navigate(AboutUsRoute) },
                toLogIn = { navController.navigate(LogInRoute) },
                toPreferences = { navController.navigate(PreferencesRoute) },
                toSignIn = { navController.navigate(SignInRoute) },
                toTermsAndConditions = { navController.navigate(TermsAndConditionsRoute) },
                toTripList = { navController.navigate(TripListScreenRoute) },
                toMainPage = { navController.navigate(MainPageScreenRoute) },
                toUserSettings = { navController.navigate(UserSettingsRoute) },
            )
        }
        composable<AboutUsRoute> {
            AboutUsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<LogInRoute> {
            LoginScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<PreferencesRoute> {
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<UserSettingsRoute> {
            UserSettingsScreen(
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
                viewModel = sharedViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<MainPageScreenRoute> {
            MainPage(
                toAboutUs = { navController.navigate(AboutUsRoute) },
                toLogIn = { navController.navigate(LogInRoute) },
                toPreferences = { navController.navigate(PreferencesRoute) },
                toSignIn = { navController.navigate(SignInRoute) },
                toTermsAndConditions = { navController.navigate(TermsAndConditionsRoute) },
                toTripList = { navController.navigate(TripListScreenRoute) },
                toUserSettings = { navController.navigate(UserSettingsRoute) },
            )
        }
    }
}