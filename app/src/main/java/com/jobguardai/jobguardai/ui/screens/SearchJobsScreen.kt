package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import com.jobguardai.jobguardai.ui.theme.BluePrimary
import com.jobguardai.jobguardai.ui.theme.BorderColor
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.TypedBlackText

@Composable
fun SearchJobsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val jobs = listOf(
        Job(
            id = "job_1",
            title = "Senior Android Developer",
            company = "TechCorp Solutions",
            location = "San Francisco, CA",
            type = "Full-Time",
            salary = "$130k - $160k",
            experience = "3-5 yrs",
            description = "Kotlin Compose MVVM.",
            skills = listOf("Kotlin", "Compose"),
            matchScore = 94
        ),
        Job(
            id = "job_2",
            title = "Mobile UI/UX Engineer",
            company = "InnovateX Labs",
            location = "Remote",
            type = "Remote",
            salary = "$110k - $140k",
            experience = "2-4 yrs",
            description = "Design UI with Compose.",
            skills = listOf("Compose", "Material 3"),
            matchScore = 88
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        val maxContainerWidth = if (maxWidth > 600.dp) 650.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .widthIn(max = maxContainerWidth)
                .align(Alignment.TopCenter)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(text = "Search title, skill or company...", color = TextSecondary) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        color = TypedBlackText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TypedBlackText,
                        unfocusedTextColor = TypedBlackText,
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = BluePrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { navController.navigate(ScreenRoutes.JobFilters.route) }) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "${jobs.size} Jobs Available", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(jobs) { job ->
                    JobCard(
                        job = job,
                        onClick = { navController.navigate(ScreenRoutes.JobDetails.createRoute(job.id)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchJobsScreenPreview() {
    JobguardAiTheme {
        SearchJobsScreen(rememberNavController())
    }
}
