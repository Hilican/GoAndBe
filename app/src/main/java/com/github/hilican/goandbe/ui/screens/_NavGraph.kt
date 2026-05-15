package com.github.hilican.goandbe.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import com.github.hilican.goandbe.ui.viewmodels.TripListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tripListViewModel: TripListViewModel,
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
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(MainPageScreenRoute)}
            )
        }
        composable<PreferencesRoute> {
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<UserSettingsRoute> {
            UserInfoScreen(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(MainPageScreenRoute)}
            )
        }
        composable<SignInRoute> {
            SignInScreen(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(MainPageScreenRoute)}
            )
        }
        composable<TermsAndConditionsRoute> {
            TermsAndConditionsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<TripListScreenRoute> {
            TripListScreen(
                viewModel = tripListViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<MainPageScreenRoute> {
            MainPage(
                viewModel = authViewModel,
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