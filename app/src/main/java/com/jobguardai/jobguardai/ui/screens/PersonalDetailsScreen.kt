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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.EmeraldSecondary
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.viewmodel.JobSeekerViewModel

@Composable
fun PersonalDetailsScreen(
    navController: NavController,
    jobSeekerViewModel: JobSeekerViewModel = viewModel()
) {
    val currentProfile by jobSeekerViewModel.userProfile.collectAsState()

    var fullName by remember(currentProfile) { mutableStateOf(currentProfile?.fullName ?: "Alex Morgan") }
    var email by remember(currentProfile) { mutableStateOf(currentProfile?.email ?: "alex.morgan@example.com") }
    var phone by remember(currentProfile) { mutableStateOf(currentProfile?.phone ?: "+1 (555) 019-2834") }
    var headline by remember(currentProfile) { mutableStateOf(currentProfile?.headline ?: "Android Developer | Jetpack Compose") }
    var location by remember(currentProfile) { mutableStateOf(currentProfile?.location ?: "San Francisco, CA") }
    var bio by remember(currentProfile) { mutableStateOf(currentProfile?.bio ?: "Passionate mobile developer building modern Android apps with Kotlin and MVVM.") }

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
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            placeholder = "e.g. Alex Morgan"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "alex@example.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "+1 (555) 000-0000"
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            containerColor = EmeraldSecondary,
            onClick = {
                jobSeekerViewModel.updatePersonalDetails(
                    name = fullName.ifBlank { "Alex Morgan" },
                    email = email.ifBlank { "alex.morgan@example.com" },
                    phone = phone,
                    bio = bio,
                    headline = headline,
                    location = location
                )
                navController.navigate(ScreenRoutes.EducationDetails.route)
            }
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
