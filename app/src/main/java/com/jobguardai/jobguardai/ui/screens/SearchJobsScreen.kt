package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.ui.components.JobCard
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.BluePrimary
import com.jobguardai.jobguardai.ui.theme.BorderColor
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary
import com.jobguardai.jobguardai.ui.theme.TypedBlackText
import com.jobguardai.jobguardai.viewmodel.JobViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchJobsScreen(
    navController: NavController,
    jobViewModel: JobViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val allJobs by jobViewModel.allJobs.collectAsState()
    var displayJobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(allJobs) {
        if (searchQuery.isBlank()) {
            displayJobs = allJobs
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        val maxContainerWidth = if (maxWidth > 600.dp) 650.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxContainerWidth)
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Text(text = "Search Real Vacancies", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        scope.launch {
                            isLoading = true
                            displayJobs = jobViewModel.searchJobsOnline(query)
                            isLoading = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = "Search by role, company, skills...", color = TextSecondary) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = TypedBlackText, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TypedBlackText,
                        unfocusedTextColor = TypedBlackText,
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                IconButton(onClick = { navController.navigate(ScreenRoutes.JobFilters.route) }) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters", tint = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (displayJobs.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "No matching jobs found in MySQL database.", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                Text(text = "Results (${displayJobs.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(displayJobs) { job ->
                        JobCard(
                            job = job,
                            onClick = { navController.navigate(ScreenRoutes.JobDetails.createRoute(job.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchJobsScreenPreview() {
    JobguardAiTheme {
        SearchJobsScreen(rememberNavController())
    }
}
