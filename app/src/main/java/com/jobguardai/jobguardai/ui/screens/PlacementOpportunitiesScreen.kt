package com.jobguardai.jobguardai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jobguardai.jobguardai.data.model.Placement
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.AccentGreen
import com.jobguardai.jobguardai.ui.theme.BackgroundLight
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme
import com.jobguardai.jobguardai.ui.theme.TextPrimary
import com.jobguardai.jobguardai.ui.theme.TextSecondary

@Composable
fun PlacementOpportunitiesScreen(navController: NavController) {
    val placements = listOf(
        Placement(
            id = "place_1",
            companyName = "Microsoft Placement Drive 2025",
            driveTitle = "Campus Placement Drive - Software Engineer",
            role = "Software Development Engineer (SDE-1)",
            packageAmount = "$120,000 / annum",
            eligibilityCriteria = "CGPA 7.5+ in B.Tech CS/IT",
            driveDate = "15 Nov 2025",
            location = "On-Campus & Virtual",
            status = "Ongoing",
            description = "Microsoft Campus Recruitment Drive."
        ),
        Placement(
            id = "place_2",
            companyName = "Google University Recruitment",
            driveTitle = "Associate Android Developer Drive",
            role = "Associate Mobile Software Engineer",
            packageAmount = "$135,000 / annum",
            eligibilityCriteria = "B.Tech CS graduates",
            driveDate = "22 Nov 2025",
            location = "Virtual Drive",
            status = "Upcoming",
            description = "Google is hiring upcoming graduates."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        Text(text = "Campus Placement Drives", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(placements) { drive ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(ScreenRoutes.PlacementDriveDetails.createRoute(drive.id)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = drive.companyName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = drive.status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${drive.role} • ${drive.packageAmount}", fontSize = 13.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "📅 Drive Date: ${drive.driveDate}", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlacementOpportunitiesScreenPreview() {
    JobguardAiTheme {
        PlacementOpportunitiesScreen(rememberNavController())
    }
}
