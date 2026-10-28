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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.jobguardai.jobguardai.ui.theme.PrimaryBlue
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.WarningOrange

@Composable
fun SkillGapAnalysisScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "AI Skill Gap Analysis", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Compared with Senior Android Role requirements", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue)
                    }
                    Text(text = " Job Readiness: 80%", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryBlue)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "✅ Matched Skills:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentGreen)
                Text(text = "• Kotlin, Jetpack Compose, MVVM, Room DB", fontSize = 13.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "⚠️ Missing Skills to Learn:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WarningOrange)
                Text(text = "• Kotlin Flow & Channel\n• Dependency Injection with Hilt", fontSize = 13.sp, color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Generate AI Learning Roadmap",
            onClick = { navController.navigate(ScreenRoutes.AILearningRoadmap.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SkillGapAnalysisScreenPreview() {
    JobguardAiTheme {
        SkillGapAnalysisScreen(rememberNavController())
    }
}
