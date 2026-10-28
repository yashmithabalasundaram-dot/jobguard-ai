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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun MyProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Alex Morgan", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Android Developer | Kotlin & Jetpack Compose", fontSize = 14.sp, color = TextSecondary)
        Text(text = "San Francisco, CA • alex.morgan@example.com", fontSize = 13.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOption("Personal Details", "Edit headline, bio & contact") { navController.navigate(ScreenRoutes.PersonalDetails.route) }
        ProfileOption("Education Details", "Stanford University • B.S. CS") { navController.navigate(ScreenRoutes.EducationDetails.route) }
        ProfileOption("Skills & Preferences", "Kotlin, Compose, MVVM") { navController.navigate(ScreenRoutes.SkillsPreferences.route) }
        ProfileOption("My Resume", "Alex_Morgan_Resume_2025.pdf") { navController.navigate(ScreenRoutes.UploadResume.route) }
        ProfileOption("Settings & Privacy", "Security, consent & theme") { navController.navigate(ScreenRoutes.SettingsPrivacy.route) }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Log Out",
            onClick = { navController.navigate(ScreenRoutes.Login.route) },
            containerColor = Color.Red.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun ProfileOption(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Text(text = "›", fontSize = 20.sp, color = TextSecondary)
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
