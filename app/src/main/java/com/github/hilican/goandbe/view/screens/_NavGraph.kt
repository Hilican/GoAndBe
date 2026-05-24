package com.github.hilican.goandbe.view.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.hilican.goandbe.viewmodel.AuthViewModel
import com.github.hilican.goandbe.viewmodel.HotelViewModel
import com.github.hilican.goandbe.viewmodel.TripListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tripListViewModel: TripListViewModel,
    hotelViewModel: HotelViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = MainPageScreenRoute // The app starts here
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
                toForgottenPassword = { navController.navigate(ForgottenPasswordRoute) },
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
                onNavigateToHome = { navController.navigate(MainPageScreenRoute) },
                goToForgottenPassword = { navController.navigate(ForgottenPasswordRoute) }
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
                onBack = { navController.popBackStack() },
                tripListViewModel = tripListViewModel,
                hotelViewModel = hotelViewModel
            )
        }
        composable<ForgottenPasswordRoute> {
            ForgottenPasswordScreen(
                viewModel = authViewModel,
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
                toHotelSection = { navController.navigate(HotelsRoute)}
            )
        }
        composable<HotelsRoute> {
            HotelsScreen(
                toHotelRooms = {navController.navigate(RoomsRoute)},
                onBackClick = { navController.popBackStack() },
                hotelViewModel = hotelViewModel,
                tipListViewModel = tripListViewModel,
            )
        }
        composable<RoomsRoute> {
            RoomsScreen(
                viewModel = hotelViewModel,
                onBackClick = {navController.popBackStack()},
            )
        }
    }
}