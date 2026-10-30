package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.jobguardai.jobguardai.ui.components.InputField
import com.jobguardai.jobguardai.ui.components.PrimaryButton
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.viewmodel.JobSeekerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsPreferencesScreen(
    navController: NavController,
    viewModel: JobSeekerViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val availableSkills = listOf("Kotlin", "Jetpack Compose", "Android SDK", "MVVM", "Room", "Coroutines", "Retrofit", "Git", "Figma", "UI/UX", "Java", "Python", "SQL", "Unit Testing", "CI/CD")
    val selectedSkills = remember(userProfile) {
        val list = mutableStateListOf<String>()
        userProfile?.skillsList?.let { list.addAll(it) }
        list
    }
    var preferredRole by remember(userProfile) {
        mutableStateOf(userProfile?.preferredRoles?.firstOrNull() ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Skills & Preferences",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Step 3 of 4: Pick your skills and job preferences.",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Select Skills", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow {
            availableSkills.forEach { skill ->
                val isSelected = selectedSkills.contains(skill)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedSkills.remove(skill) else selectedSkills.add(skill)
                    },
                    label = { Text(text = skill) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            value = preferredRole,
            onValueChange = { preferredRole = it },
            label = "Target Job Role",
            placeholder = "e.g. Android Developer"
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Save & Next",
            onClick = {
                val roles = if (preferredRole.isNotBlank()) listOf(preferredRole.trim()) else emptyList()
                viewModel.updateSkillsAndPreferences(
                    skills = selectedSkills.toList(),
                    roles = roles,
                    locations = emptyList()
                )
                navController.navigate(ScreenRoutes.UploadResume.route)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SkillsPreferencesScreenPreview() {
    JobguardAiTheme {
        SkillsPreferencesScreen(rememberNavController())
    }
}
