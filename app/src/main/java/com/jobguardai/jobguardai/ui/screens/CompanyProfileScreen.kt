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
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun CompanyProfileScreen(navController: NavController, companyId: String = "comp_1") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "TechCorp Solutions", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Software & AI Solutions • San Francisco, CA", fontSize = 14.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "TechCorp Solutions is a global leader in cloud and mobile technology solutions powering fortune 500 enterprises with cutting edge AI products.",
            fontSize = 14.sp,
            color = TextPrimary,
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyProfileScreenPreview() {
    JobguardAiTheme {
        CompanyProfileScreen(rememberNavController())
    }
}
