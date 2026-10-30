package com.jobguardai.jobguardai.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val message: String? = null,
    val data: T? = null,
    val errors: Any? = null
)

data class AuthData(
    val token: String? = null,
    val user: UserDto? = null,
    val otp_code: String? = null,
    val message: String? = null
)

data class UserDto(
    val id: String,
    val full_name: String,
    val email: String,
    val phone: String? = null,
    val is_verified: Boolean = false,
    val headline: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val profile_completion_pct: Int = 20
)

data class UserProfileData(
    val user: UserDto,
    val profile: ProfileDto? = null,
    val skills: List<String> = emptyList(),
    val preferences: PreferencesDto? = null,
    val default_resume: DefaultResumeDto? = null
)

data class ProfileDto(
    val headline: String? = "",
    val bio: String? = "",
    val location: String? = "",
    val profile_image_url: String? = "",
    val profile_completion_pct: Int = 20
)

data class PreferencesDto(
    val preferred_roles: String? = "",
    val preferred_locations: String? = "",
    val preferred_types: String? = "",
    val expected_salary: String? = "",
    val consent_shared_with_companies: Boolean = true,
    val notifications_enabled: Boolean = true
)

data class DefaultResumeDto(
    val id: String,
    val file_name: String,
    val file_size: String,
    val upload_date: String
)

data class JobSearchData(
    val total: Int,
    val page: Int,
    val pages: Int,
    val limit: Int,
    val count: Int,
    val jobs: List<JobDto>
)

data class JobDto(
    val id: String,
    val title: String,
    val company: String,
    val companyLogo: String = "",
    val location: String,
    val type: String,
    val salary: String,
    val experience: String,
    val description: String,
    val requirements: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val isScamFlagged: Boolean = false,
    val scamReason: String? = null,
    val matchScore: Int = 85,
    val deadline: String = "",
    val externalUrl: String = ""
)

data class DashboardStatsData(
    val greeting: String,
    val userName: String,
    val profileCompletion: Int,
    val resumeScore: Int,
    val hasResume: Boolean,
    val resumeFileName: String? = null,
    val totalSavedJobs: Int,
    val totalApplications: Int,
    val totalPlacements: Int,
    val unreadNotifications: Int,
    val recommendedJobs: List<JobDto> = emptyList()
)

data class SkillGapData(
    val targetRole: String,
    val matchedSkills: List<String> = emptyList(),
    val missingSkills: List<String> = emptyList(),
    val readinessPercentage: Int = 0,
    val recommendedCourses: List<CourseDto> = emptyList()
)

data class CourseDto(
    val id: String,
    val title: String,
    val provider: String,
    val duration: String,
    val level: String,
    val skillCategory: String,
    val progressPercent: Int = 0,
    val url: String = "",
    val description: String = "",
    val modules: List<String> = emptyList(),
    val isCompleted: Boolean = false
)

data class SavedToggleData(
    val job_id: String,
    val is_saved: Boolean
)

