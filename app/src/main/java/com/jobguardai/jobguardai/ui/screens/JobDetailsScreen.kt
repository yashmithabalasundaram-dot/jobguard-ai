package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.AccentGreen
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.WarningRed

@Composable
fun JobDetailsScreen(navController: NavController, jobId: String = "job_1") {
    val isScam = jobId == "job_4"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Senior Android Developer", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "TechCorp Solutions • San Francisco, CA", fontSize = 14.sp, color = TextSecondary)

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
                    Text(text = "SCAM WARNING: Requires upfront fee ($50). Do not pay money.", color = WarningRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    Text(text = "VERIFIED SAFE: High match score (94%) with your Android skills.", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Job Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We are seeking a talented Senior Android Developer to lead our mobile team in building next-gen AI driven mobile applications using Jetpack Compose, Kotlin Coroutines, and Clean MVVM Architecture.",
            fontSize = 14.sp,
            color = TextPrimary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Apply Now",
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
