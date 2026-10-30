package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.ui.components.JobCard
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.AccessibleSuccessGreen
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.DarkCharcoalText
import com.jobguardai.jobguardai.ui.theme.DeepTealPrimary
import com.jobguardai.jobguardai.ui.theme.EmeraldSecondary
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.MintGreenHighlight
import com.jobguardai.jobguardai.ui.theme.MutedGreyGreenText
import com.jobguardai.jobguardai.ui.theme.SubtleGreyGreenBorder
import com.jobguardai.jobguardai.viewmodel.DashboardUiState
import com.jobguardai.jobguardai.viewmodel.DashboardViewModel

@Composable
fun HomeDashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    val greeting = when (val state = uiState) {
        is DashboardUiState.Success -> state.stats.greeting
        else -> "Welcome to JobGuard AI 👋"
    }

    val profilePct = when (val state = uiState) {
        is DashboardUiState.Success -> state.stats.profileCompletion
        else -> 0
    }

    val savedCount = when (val state = uiState) {
        is DashboardUiState.Success -> state.stats.totalSavedJobs.toString()
        else -> "0"
    }

    val appCount = when (val state = uiState) {
        is DashboardUiState.Success -> state.stats.totalApplications.toString()
        else -> "0"
    }

    val placementCount = when (val state = uiState) {
        is DashboardUiState.Success -> state.stats.totalPlacements.toString()
        else -> "0"
    }

    val jobsList = when (val state = uiState) {
        is DashboardUiState.Success -> state.recommendedJobs
        else -> emptyList()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        val maxContainerWidth = if (maxWidth > 600.dp) 650.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxContainerWidth)
                .align(Alignment.TopCenter)
        ) {
            // Deep Teal Welcome Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepTealPrimary)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MintGreenHighlight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Guard Active • Live MySQL Backend",
                                fontSize = 12.sp,
                                color = MintGreenHighlight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { navController.navigate(ScreenRoutes.Notifications.route) }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        IconButton(onClick = { navController.navigate(ScreenRoutes.MyProfile.route) }) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // White Search Bar Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(ScreenRoutes.SearchJobs.route) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SubtleGreyGreenBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MutedGreyGreenText)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search Android jobs, skills, companies...",
                            color = MutedGreyGreenText,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mint Green AI Resume Matching Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MintGreenHighlight),
                    border = BorderStroke(1.dp, SubtleGreyGreenBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Resume Match Score",
                                color = DeepTealPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$profilePct% Profile Optimization",
                            color = DarkCharcoalText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (profilePct > 30) "Your profile is optimized for verified engineering vacancies." else "Complete your profile & upload a resume to maximize match score.",
                            color = MutedGreyGreenText,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { navController.navigate(ScreenRoutes.UploadResume.route) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSecondary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Analyze & Optimize Resume",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Stats Row (Real Backend Counts)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Saved Jobs", savedCount, Modifier.weight(1f)) { navController.navigate(ScreenRoutes.SavedJobs.route) }
                    StatCard("Applications", appCount, Modifier.weight(1f)) { navController.navigate(ScreenRoutes.ApplicationTracker.route) }
                    StatCard("Placements", placementCount, Modifier.weight(1f)) { navController.navigate(ScreenRoutes.PlacementOpportunities.route) }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickNavChip("Search Jobs", Icons.Default.Search) { navController.navigate(ScreenRoutes.SearchJobs.route) }
                    QuickNavChip("Upload Resume", Icons.Default.UploadFile) { navController.navigate(ScreenRoutes.UploadResume.route) }
                    QuickNavChip("Placements", Icons.Default.School) { navController.navigate(ScreenRoutes.PlacementOpportunities.route) }
                    QuickNavChip("Skill Gaps", Icons.Default.AutoAwesome) { navController.navigate(ScreenRoutes.SkillGapAnalysis.route) }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Recommended Jobs for You",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoalText
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (uiState is DashboardUiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EmeraldSecondary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(jobsList) { job ->
                            JobCard(
                                job = job,
                                onClick = { navController.navigate(ScreenRoutes.JobDetails.createRoute(job.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, count: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SubtleGreyGreenBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldSecondary)
            Text(text = label, fontSize = 11.sp, color = MutedGreyGreenText)
        }
    }
}

@Composable
fun QuickNavChip(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(EmeraldSecondary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = EmeraldSecondary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = DarkCharcoalText)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardScreenPreview() {
    JobguardAiTheme {
        HomeDashboardScreen(rememberNavController())
    }
}
