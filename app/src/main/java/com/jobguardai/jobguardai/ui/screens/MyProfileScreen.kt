package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.viewmodel.AuthViewModel

@Composable
fun MyProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val user by authViewModel.currentUser.collectAsState()

    val name = user?.fullName?.ifBlank { "JobGuard Member" } ?: "JobGuard Member"
    val headline = user?.headline?.ifBlank { "Add your professional headline" } ?: "Add your professional headline"
    val location = user?.location?.ifBlank { "Location not set" } ?: "Location not set"
    val email = user?.email ?: ""

    val skillsSummary = if (!user?.skillsList.isNullOrEmpty()) {
        user!!.skillsList.take(3).joinToString(", ")
    } else {
        "Add technical skills & preferences"
    }

    val eduSummary = if (!user?.educationList.isNullOrEmpty()) {
        "${user!!.educationList.size} education record(s) added"
    } else {
        "Add your educational background"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = headline, fontSize = 14.sp, color = TextSecondary)
        if (email.isNotBlank()) {
            Text(text = if (location.isNotBlank()) "$location • $email" else email, fontSize = 13.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOption("Personal Details", "Edit headline, bio & contact") { navController.navigate(ScreenRoutes.PersonalDetails.route) }
        ProfileOption("Education Details", eduSummary) { navController.navigate(ScreenRoutes.EducationDetails.route) }
        ProfileOption("Skills & Preferences", skillsSummary) { navController.navigate(ScreenRoutes.SkillsPreferences.route) }
        ProfileOption("My Resume", "Upload and view ATS analysis") { navController.navigate(ScreenRoutes.UploadResume.route) }
        ProfileOption("Settings & Privacy", "Security, consent & theme") { navController.navigate(ScreenRoutes.SettingsPrivacy.route) }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Log Out",
            onClick = {
                authViewModel.logout()
                navController.navigate(ScreenRoutes.Login.route) {
                    popUpTo(ScreenRoutes.HomeDashboard.route) { inclusive = true }
                }
            },
            containerColor = Color.Red.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun ProfileOption(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyProfileScreenPreview() {
    JobguardAiTheme {
        MyProfileScreen(rememberNavController())
    }
}
