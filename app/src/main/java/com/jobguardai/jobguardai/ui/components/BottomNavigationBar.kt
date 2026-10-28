package com.jobguardai.jobguardai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jobguardai.jobguardai.ui.navigation.ScreenRoutes
import com.jobguardai.jobguardai.ui.theme.DeepTealPrimary
import com.jobguardai.jobguardai.ui.theme.EmeraldSecondary
import com.jobguardai.jobguardai.ui.theme.MutedGreyGreenText
import com.jobguardai.jobguardai.ui.theme.PureWhiteCard

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", ScreenRoutes.HomeDashboard.route, Icons.Default.Home),
    BottomNavItem("Search", ScreenRoutes.SearchJobs.route, Icons.Default.Search),
    BottomNavItem("Saved", ScreenRoutes.SavedJobs.route, Icons.Default.Bookmark),
    BottomNavItem("Apps", ScreenRoutes.ApplicationTracker.route, Icons.AutoMirrored.Filled.Assignment),
    BottomNavItem("Profile", ScreenRoutes.MyProfile.route, Icons.Default.Person)
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom nav on primary tab destinations
    val mainRoutes = bottomNavItems.map { it.route }
    if (currentRoute !in mainRoutes) return

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = PureWhiteCard,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) DeepTealPrimary else MutedGreyGreenText
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) DeepTealPrimary else MutedGreyGreenText
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = EmeraldSecondary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
