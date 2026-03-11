package com.github.hilican.goandbe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToAllScreens(
    toAboutUs: () -> Unit,
    toLogIn: () -> Unit,
    toPreferences: () -> Unit,
    toSignIn: () -> Unit,
    toTermsAndConditions: () -> Unit,
    toTripList: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalGap()
        FloatingActionButton(
            onClick = toAboutUs,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "About Us",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        VerticalGap()
        FloatingActionButton(
            onClick = toLogIn,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        VerticalGap()
        FloatingActionButton(
            onClick = toPreferences,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Preferences",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        VerticalGap()
        FloatingActionButton(
            onClick = toSignIn,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Sign In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        VerticalGap()
        FloatingActionButton(
            onClick = toTermsAndConditions,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Terms & Conditions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        VerticalGap()
        FloatingActionButton(
            onClick = toTripList,
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Trip List",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}