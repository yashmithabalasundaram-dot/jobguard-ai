package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun EducationDetailsScreen(navController: NavController) {
    var degree by remember { mutableStateOf("B.S. Computer Science") }
    var college by remember { mutableStateOf("Stanford University") }
    var gradYear by remember { mutableStateOf("2024") }
    var grade by remember { mutableStateOf("3.8 / 4.0 GPA") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Education Details",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Step 2 of 4: Educational qualifications and achievements.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        InputField(
            value = degree,
            onValueChange = { degree = it },
            label = "Degree / Course",
            placeholder = "e.g. B.Tech Computer Science"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = college,
            onValueChange = { college = it },
            label = "Institution / University",
            placeholder = "e.g. Stanford University"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = gradYear,
            onValueChange = { gradYear = it },
            label = "Graduation Year",
            placeholder = "e.g. 2024"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = grade,
            onValueChange = { grade = it },
            label = "Grade / CGPA",
            placeholder = "e.g. 8.5 CGPA"
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Save & Next",
            onClick = { navController.navigate(ScreenRoutes.SkillsPreferences.route) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EducationDetailsScreenPreview() {
    JobguardAiTheme {
        EducationDetailsScreen(rememberNavController())
    }
}
