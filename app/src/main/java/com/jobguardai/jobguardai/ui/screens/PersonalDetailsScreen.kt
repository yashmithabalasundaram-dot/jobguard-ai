package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun PersonalDetailsScreen(navController: NavController) {
    var headline by remember { mutableStateOf("Android Developer | Jetpack Compose") }
    var location by remember { mutableStateOf("San Francisco, CA") }
    var bio by remember { mutableStateOf("Passionate mobile developer building modern Android apps with Kotlin and MVVM.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Personal Details",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Step 1 of 4: Help recruiters know your professional identity.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        InputField(
            value = headline,
            onValueChange = { headline = it },
            label = "Professional Headline",
            placeholder = "e.g. Senior Android Engineer"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = location,
            onValueChange = { location = it },
            label = "Current Location",
            placeholder = "City, State / Country"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = bio,
            onValueChange = { bio = it },
            label = "Brief Bio",
            placeholder = "Tell recruiters about your background...",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Save & Next",
            onClick = { navController.navigate(ScreenRoutes.EducationDetails.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalDetailsScreenPreview() {
    JobguardAiTheme {
        PersonalDetailsScreen(rememberNavController())
    }
}
