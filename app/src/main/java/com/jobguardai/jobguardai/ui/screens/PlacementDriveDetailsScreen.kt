package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun PlacementDriveDetailsScreen(navController: NavController, driveId: String = "place_1") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Microsoft Placement Drive 2025", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Role: Software Development Engineer (SDE-1)", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Eligibility Criteria", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "CGPA 7.5+ in B.Tech / M.Tech CS/IT (Batch 2025/2026)", fontSize = 14.sp, color = TextPrimary)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selection Rounds", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "1. Aptitude & Coding\n2. Technical Round 1\n3. Technical Round 2\n4. HR Round", fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Register for Placement Drive",
            onClick = { navController.navigate(ScreenRoutes.ApplicationSubmitted.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlacementDriveDetailsScreenPreview() {
    JobguardAiTheme {
        PlacementDriveDetailsScreen(rememberNavController())
    }
}
