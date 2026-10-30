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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.data.model.Education
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.viewmodel.JobSeekerViewModel

@Composable
fun EducationDetailsScreen(
    navController: NavController,
    viewModel: JobSeekerViewModel = viewModel()
) {
    var degree by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var gradYear by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }

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
            label = "College / University",
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
            label = "Grade / Percentage / CGPA",
            placeholder = "e.g. 3.8 / 4.0 GPA or 85%"
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Save & Next",
            onClick = {
                if (degree.isNotBlank() || college.isNotBlank()) {
                    val edu = Education(
                        degree = degree.trim(),
                        institution = college.trim(),
                        fieldOfStudy = "Computer Science",
                        startYear = (gradYear.toIntOrNull()?.let { (it - 4).toString() }) ?: "",
                        endYear = gradYear.trim(),
                        grade = grade.trim()
                    )
                    viewModel.updateEducation(listOf(edu))
                }
                navController.navigate(ScreenRoutes.SkillsPreferences.route)
            }
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
