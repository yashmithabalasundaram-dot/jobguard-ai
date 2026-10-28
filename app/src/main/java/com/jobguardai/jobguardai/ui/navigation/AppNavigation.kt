package com.jobguardai.jobguardai.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobguardai.jobguardai.ui.components.BottomNavigationBar
import com.jobguardai.jobguardai.ui.screens.AIAnalysisProgressScreen
import com.jobguardai.jobguardai.ui.screens.AILearningRoadmapScreen
import com.jobguardai.jobguardai.ui.screens.AIResumeBuilderScreen
import com.jobguardai.jobguardai.ui.screens.ApplicationSubmittedScreen
import com.jobguardai.jobguardai.ui.screens.ApplicationTrackerScreen
import com.jobguardai.jobguardai.ui.screens.CompanyProfileScreen
import com.jobguardai.jobguardai.ui.screens.CourseDetailsScreen
import com.jobguardai.jobguardai.ui.screens.CreateAccountScreen
import com.jobguardai.jobguardai.ui.screens.EducationDetailsScreen
import com.jobguardai.jobguardai.ui.screens.HomeDashboardScreen
import com.jobguardai.jobguardai.ui.screens.JobDetailsScreen
import com.jobguardai.jobguardai.ui.screens.JobFiltersScreen
import com.jobguardai.jobguardai.ui.screens.LoginScreen
import com.jobguardai.jobguardai.ui.screens.MyProfileScreen
import com.jobguardai.jobguardai.ui.screens.NotificationsScreen
import com.jobguardai.jobguardai.ui.screens.OnboardingScreen
import com.jobguardai.jobguardai.ui.screens.PersonalDetailsScreen
import com.jobguardai.jobguardai.ui.screens.PlacementDriveDetailsScreen
import com.jobguardai.jobguardai.ui.screens.PlacementOpportunitiesScreen
import com.jobguardai.jobguardai.ui.screens.ResetPasswordScreen
import com.jobguardai.jobguardai.ui.screens.ResumeAnalysisResultsScreen
import com.jobguardai.jobguardai.ui.screens.ReviewApplicationScreen
import com.jobguardai.jobguardai.ui.screens.SavedJobsScreen
import com.jobguardai.jobguardai.ui.screens.SearchJobsScreen
import com.jobguardai.jobguardai.ui.screens.SettingsPrivacyScreen
import com.jobguardai.jobguardai.ui.screens.SkillGapAnalysisScreen
import com.jobguardai.jobguardai.ui.screens.SkillsPreferencesScreen
import com.jobguardai.jobguardai.ui.screens.SplashScreen
import com.jobguardai.jobguardai.ui.screens.UploadResumeScreen
import com.jobguardai.jobguardai.ui.screens.VerifyEmailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = ScreenRoutes.Splash.route
            ) {
                composable(ScreenRoutes.Splash.route) {
                    SplashScreen(navController = navController)
                }
                composable(ScreenRoutes.Onboarding.route) {
                    OnboardingScreen(navController = navController)
                }
                composable(ScreenRoutes.CreateAccount.route) {
                    CreateAccountScreen(navController = navController)
                }
                composable(ScreenRoutes.Login.route) {
                    LoginScreen(navController = navController)
                }
                composable(ScreenRoutes.VerifyEmail.route) {
                    VerifyEmailScreen(navController = navController)
                }
                composable(ScreenRoutes.ResetPassword.route) {
                    ResetPasswordScreen(navController = navController)
                }
                composable(ScreenRoutes.PersonalDetails.route) {
                    PersonalDetailsScreen(navController = navController)
                }
                composable(ScreenRoutes.EducationDetails.route) {
                    EducationDetailsScreen(navController = navController)
                }
                composable(ScreenRoutes.SkillsPreferences.route) {
                    SkillsPreferencesScreen(navController = navController)
                }
                composable(ScreenRoutes.UploadResume.route) {
                    UploadResumeScreen(navController = navController)
                }
                composable(ScreenRoutes.AIAnalysisProgress.route) {
                    AIAnalysisProgressScreen(navController = navController)
                }
                composable(ScreenRoutes.ResumeAnalysisResults.route) {
                    ResumeAnalysisResultsScreen(navController = navController)
                }
                composable(ScreenRoutes.HomeDashboard.route) {
                    HomeDashboardScreen(navController = navController)
                }
                composable(ScreenRoutes.SearchJobs.route) {
                    SearchJobsScreen(navController = navController)
                }
                composable(ScreenRoutes.JobFilters.route) {
                    JobFiltersScreen(navController = navController)
                }
                composable(
                    route = ScreenRoutes.JobDetails.route,
                    arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getString("jobId") ?: "job_1"
                    JobDetailsScreen(navController = navController, jobId = jobId)
                }
                composable(ScreenRoutes.SavedJobs.route) {
                    SavedJobsScreen(navController = navController)
                }
                composable(
                    route = ScreenRoutes.CompanyProfile.route,
                    arguments = listOf(navArgument("companyId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val companyId = backStackEntry.arguments?.getString("companyId") ?: "comp_1"
                    CompanyProfileScreen(navController = navController, companyId = companyId)
                }
                composable(ScreenRoutes.PlacementOpportunities.route) {
                    PlacementOpportunitiesScreen(navController = navController)
                }
                composable(
                    route = ScreenRoutes.PlacementDriveDetails.route,
                    arguments = listOf(navArgument("driveId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val driveId = backStackEntry.arguments?.getString("driveId") ?: "place_1"
                    PlacementDriveDetailsScreen(navController = navController, driveId = driveId)
                }
                composable(
                    route = ScreenRoutes.ReviewApplication.route,
                    arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getString("jobId") ?: "job_1"
                    ReviewApplicationScreen(navController = navController, jobId = jobId)
                }
                composable(ScreenRoutes.ApplicationSubmitted.route) {
                    ApplicationSubmittedScreen(navController = navController)
                }
                composable(ScreenRoutes.ApplicationTracker.route) {
                    ApplicationTrackerScreen(navController = navController)
                }
                composable(ScreenRoutes.SkillGapAnalysis.route) {
                    SkillGapAnalysisScreen(navController = navController)
                }
                composable(ScreenRoutes.AILearningRoadmap.route) {
                    AILearningRoadmapScreen(navController = navController)
                }
                composable(
                    route = ScreenRoutes.CourseDetails.route,
                    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val courseId = backStackEntry.arguments?.getString("courseId") ?: "course_1"
                    CourseDetailsScreen(navController = navController, courseId = courseId)
                }
                composable(ScreenRoutes.AIResumeBuilder.route) {
                    AIResumeBuilderScreen(navController = navController)
                }
                composable(ScreenRoutes.Notifications.route) {
                    NotificationsScreen(navController = navController)
                }
                composable(ScreenRoutes.MyProfile.route) {
                    MyProfileScreen(navController = navController)
                }
                composable(ScreenRoutes.SettingsPrivacy.route) {
                    SettingsPrivacyScreen(navController = navController)
                }
            }
        }
    }
}
