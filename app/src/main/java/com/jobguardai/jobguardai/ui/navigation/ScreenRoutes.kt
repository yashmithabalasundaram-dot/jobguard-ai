package com.jobguardai.jobguardai.ui.navigation

sealed class ScreenRoutes(val route: String) {
    object Splash : ScreenRoutes("splash")
    object Onboarding : ScreenRoutes("onboarding")
    object CreateAccount : ScreenRoutes("create_account")
    object Login : ScreenRoutes("login")
    object VerifyEmail : ScreenRoutes("verify_email")
    object ResetPassword : ScreenRoutes("reset_password")
    object PersonalDetails : ScreenRoutes("personal_details")
    object EducationDetails : ScreenRoutes("education_details")
    object SkillsPreferences : ScreenRoutes("skills_preferences")
    object UploadResume : ScreenRoutes("upload_resume")
    object AIAnalysisProgress : ScreenRoutes("ai_analysis_progress")
    object ResumeAnalysisResults : ScreenRoutes("resume_analysis_results")
    object HomeDashboard : ScreenRoutes("home_dashboard")
    object SearchJobs : ScreenRoutes("search_jobs")
    object JobFilters : ScreenRoutes("job_filters")
    object JobDetails : ScreenRoutes("job_details/{jobId}") {
        fun createRoute(jobId: String) = "job_details/$jobId"
    }
    object SavedJobs : ScreenRoutes("saved_jobs")
    object CompanyProfile : ScreenRoutes("company_profile/{companyId}") {
        fun createRoute(companyId: String) = "company_profile/$companyId"
    }
    object PlacementOpportunities : ScreenRoutes("placement_opportunities")
    object PlacementDriveDetails : ScreenRoutes("placement_drive_details/{driveId}") {
        fun createRoute(driveId: String) = "placement_drive_details/$driveId"
    }
    object ReviewApplication : ScreenRoutes("review_application/{jobId}") {
        fun createRoute(jobId: String) = "review_application/$jobId"
    }
    object ApplicationSubmitted : ScreenRoutes("application_submitted")
    object ApplicationTracker : ScreenRoutes("application_tracker")
    object SkillGapAnalysis : ScreenRoutes("skill_gap_analysis")
    object AILearningRoadmap : ScreenRoutes("ai_learning_roadmap")
    object CourseDetails : ScreenRoutes("course_details/{courseId}") {
        fun createRoute(courseId: String) = "course_details/$courseId"
    }
    object AIResumeBuilder : ScreenRoutes("ai_resume_builder")
    object Notifications : ScreenRoutes("notifications")
    object MyProfile : ScreenRoutes("my_profile")
    object SettingsPrivacy : ScreenRoutes("settings_privacy")
}
