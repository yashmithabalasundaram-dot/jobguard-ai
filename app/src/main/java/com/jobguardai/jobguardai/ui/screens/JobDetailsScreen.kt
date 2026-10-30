package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.jobguardai.jobguardai.ui.theme.AccentGreen
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.DeepTealPrimary
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.WarningRed
import com.jobguardai.jobguardai.viewmodel.JobViewModel

@Composable
fun JobDetailsScreen(
    navController: NavController,
    jobId: String = "job_1",
    jobViewModel: JobViewModel = viewModel()
) {
    LaunchedEffect(jobId) {
        jobViewModel.selectJob(jobId)
    }

    val selectedJob by jobViewModel.selectedJob.collectAsState()

    val title = selectedJob?.title ?: "Senior Android Developer"
    val company = selectedJob?.effectiveCompany ?: "TechCorp Solutions"
    val location = selectedJob?.location ?: "San Francisco, CA"
    val description = selectedJob?.description ?: "We are seeking a talented Senior Android Developer to lead our mobile team in building next-gen AI driven mobile applications using Jetpack Compose, Kotlin Coroutines, and Clean MVVM Architecture."
    val isScam = selectedJob?.isScamFlagged ?: (jobId == "job_4")
    val isSaved = selectedJob?.isSaved ?: false
    val scamReason = selectedJob?.scamReason ?: "Requires upfront fee. Do not pay money."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "$company • $location", fontSize = 14.sp, color = TextSecondary)
            }

            IconButton(onClick = {
                selectedJob?.let { jobViewModel.toggleSave(it) }
            }) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save Job",
                    tint = if (isSaved) DeepTealPrimary else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isScam) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WarningRed.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SCAM WARNING: $scamReason",
                        color = WarningRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AccentGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "VERIFIED SAFE: High match score (${selectedJob?.matchScore ?: 85}%) with your Android profile.",
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Job Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = TextPrimary,
            lineHeight = 22.sp
        )

        val requirements = selectedJob?.requirements ?: emptyList()
        if (requirements.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "Key Requirements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            requirements.forEach { req ->
                Text(text = "• $req", fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Review & Apply Now",
            onClick = { navController.navigate(ScreenRoutes.ReviewApplication.createRoute(jobId)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JobDetailsScreenPreview() {
    JobguardAiTheme {
        JobDetailsScreen(rememberNavController())
    }
}
