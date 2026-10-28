package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.ui.components.JobCard
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary

@Composable
fun SavedJobsScreen(navController: NavController) {
    val savedJobs = listOf(
        Job(
            id = "job_2",
            title = "Mobile UI/UX Engineer",
            company = "InnovateX Labs",
            location = "Remote",
            type = "Remote",
            salary = "$110k - $140k",
            experience = "2-4 yrs",
            description = "Build interactive mobile designs with Material 3.",
            skills = listOf("Jetpack Compose", "Material 3"),
            isSaved = true,
            matchScore = 88
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        Text(text = "Saved Jobs (1)", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(savedJobs) { job ->
                JobCard(
                    job = job,
                    onClick = { navController.navigate(ScreenRoutes.JobDetails.createRoute(job.id)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedJobsScreenPreview() {
    JobguardAiTheme {
        SavedJobsScreen(rememberNavController())
    }
}
