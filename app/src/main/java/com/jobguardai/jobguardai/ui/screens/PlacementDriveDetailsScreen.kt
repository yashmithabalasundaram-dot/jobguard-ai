package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.jobguardai.jobguardai.viewmodel.PlacementViewModel

@Composable
fun PlacementDriveDetailsScreen(
    navController: NavController,
    driveId: String = "place_1",
    viewModel: PlacementViewModel = viewModel()
) {
    LaunchedEffect(driveId) {
        viewModel.selectPlacement(driveId)
    }

    val selectedDrive by viewModel.selectedPlacement.collectAsState()

    val company = selectedDrive?.companyName ?: "Microsoft Placement Drive 2025"
    val role = selectedDrive?.role ?: "Software Development Engineer (SDE-1)"
    val criteria = selectedDrive?.eligibilityCriteria ?: "CGPA 7.5+ in B.Tech / M.Tech CS/IT (Batch 2025/2026)"
    val rounds = selectedDrive?.rounds ?: listOf("Aptitude & Coding Assessment", "Technical Interview Round 1", "Technical Interview Round 2", "HR Round")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = company, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Role: $role", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Eligibility Criteria", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = criteria, fontSize = 14.sp, color = TextPrimary)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selection Rounds", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        val roundsText = rounds.mapIndexed { idx, r -> "${idx + 1}. $r" }.joinToString("\n")
        Text(text = roundsText, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Register for Campus Drive",
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
