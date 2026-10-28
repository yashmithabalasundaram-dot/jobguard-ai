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
fun AIResumeBuilderScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "AI Resume Builder", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Generate professional ATS-friendly resume drafts in minutes.", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Generate Resume Draft",
            onClick = { navController.navigate(ScreenRoutes.ResumeAnalysisResults.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AIResumeBuilderScreenPreview() {
    JobguardAiTheme {
        AIResumeBuilderScreen(rememberNavController())
    }
}
