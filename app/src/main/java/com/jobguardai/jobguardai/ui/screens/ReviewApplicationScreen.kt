package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun ReviewApplicationScreen(navController: NavController, jobId: String = "job_1") {
    var coverNote by remember { mutableStateOf("I am excited to submit my application for this role. My expertise in Jetpack Compose and Kotlin Coroutines aligns with your requirements.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Review Application", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Senior Android Developer • TechCorp Solutions", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Attached Resume", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📄 Alex_Morgan_Resume_2025.pdf (88/100 AI Score)", fontSize = 13.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            value = coverNote,
            onValueChange = { coverNote = it },
            label = "Optional Cover Note",
            placeholder = "Add a quick note to the recruiter...",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Submit Application",
            onClick = { navController.navigate(ScreenRoutes.ApplicationSubmitted.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewApplicationScreenPreview() {
    JobguardAiTheme {
        ReviewApplicationScreen(rememberNavController())
    }
}
