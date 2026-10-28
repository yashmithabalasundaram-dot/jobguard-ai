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
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun CourseDetailsScreen(navController: NavController, courseId: String = "course_1") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Mastering Kotlin Flow & Channels", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "JobGuard AI Academy • 4 Hours", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Course Description", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Learn asynchronous stream processing with StateFlow, SharedFlow, and Channels in production Android apps.",
            fontSize = 14.sp,
            color = TextPrimary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Start Lesson Now",
            onClick = { navController.popBackStack() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CourseDetailsScreenPreview() {
    JobguardAiTheme {
        CourseDetailsScreen(rememberNavController())
    }
}
