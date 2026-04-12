package com.github.hilican.goandbe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.hilican.goandbe.ui.theme.GoAndBeTheme

@Composable
fun ToAllScreens(
    toAboutUs: () -> Unit,
    toLogIn: () -> Unit,
    toPreferences: () -> Unit,
    toSignIn: () -> Unit,
    toTermsAndConditions: () -> Unit,
    toTripList: () -> Unit,
    toMainPage: () -> Unit,
    toUserSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalGap()
        NavigationButton("About Us",toAboutUs)
        VerticalGap()
        NavigationButton("Log In",toLogIn)
        VerticalGap()
        NavigationButton("Preferences",toPreferences)
        VerticalGap()
        NavigationButton("Sign In",toSignIn)
        VerticalGap()
        NavigationButton("Terms & Conditions",toTermsAndConditions)
        VerticalGap()
        NavigationButton("Terms & Conditions",toTermsAndConditions)
        VerticalGap()
        NavigationButton("Trip List",toTripList)
        VerticalGap()
        NavigationButton("MainScreen",toMainPage)
        VerticalGap()
        NavigationButton("User Settings",toUserSettings)
    }
}

@Preview(showBackground = true)
@Composable
private fun preview()
{
    GoAndBeTheme {
        ToAllScreens(
            toAboutUs = {},
            toLogIn = {},
            toPreferences = {},
            toSignIn = {},
            toTermsAndConditions = {},
            toTripList = {},
            toMainPage = {},
            toUserSettings = {},
        )
    }
}