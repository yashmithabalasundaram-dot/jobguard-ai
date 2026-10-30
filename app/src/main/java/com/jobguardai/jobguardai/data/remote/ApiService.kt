package com.jobguardai.jobguardai.data.remote

import com.jobguardai.jobguardai.data.model.Company
import com.jobguardai.jobguardai.data.model.Course
import com.jobguardai.jobguardai.data.model.Education
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.model.JobApplication
import com.jobguardai.jobguardai.data.model.NotificationItem
import com.jobguardai.jobguardai.data.model.Placement
import com.jobguardai.jobguardai.data.model.Resume
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // --- Authentication ---
    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/verify_email.php")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<ApiResponse<Map<String, Any>>>

    @POST("auth/resend_otp.php")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<ApiResponse<Map<String, String>>>

    @POST("auth/forgot_password.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Map<String, String>>>

    @POST("auth/reset_password.php")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Any>>

    @POST("auth/change_password.php")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Any>>

    @GET("auth/me.php")
    suspend fun getCurrentUser(): Response<ApiResponse<UserProfileData>>

    @POST("auth/logout.php")
    suspend fun logout(): Response<ApiResponse<Any>>

    // --- Profile & Personal Details ---
    @GET("profile/get.php")
    suspend fun getProfile(): Response<ApiResponse<Map<String, Any>>>

    @POST("profile/update.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<Map<String, Any>>>

    @GET("profile/preferences.php")
    suspend fun getPreferences(): Response<ApiResponse<PreferencesDto>>

    @POST("profile/preferences.php")
    suspend fun updatePreferences(@Body request: UpdatePreferencesRequest): Response<ApiResponse<PreferencesDto>>

    // --- Education ---
    @GET("education/list.php")
    suspend fun getEducationList(): Response<ApiResponse<List<Education>>>

    @POST("education/save.php")
    suspend fun saveEducation(@Body request: SaveEducationRequest): Response<ApiResponse<Education>>

    @POST("education/delete.php")
    suspend fun deleteEducation(@Body request: IdRequest): Response<ApiResponse<Any>>

    // --- Skills ---
    @GET("skills/list.php")
    suspend fun getSkills(): Response<ApiResponse<Map<String, Any>>>

    @POST("skills/update.php")
    suspend fun updateSkills(@Body request: UpdateSkillsRequest): Response<ApiResponse<Map<String, Any>>>

    @GET("skills/available.php")
    suspend fun getAvailableSkills(): Response<ApiResponse<List<String>>>

    // --- Jobs ---
    @GET("jobs/search.php")
    suspend fun searchJobs(
        @Query("q") query: String? = null,
        @Query("type") type: String? = null,
        @Query("location") location: String? = null,
        @Query("experience") experience: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<JobSearchData>>

    @GET("jobs/detail.php")
    suspend fun getJobById(@Query("id") id: String): Response<ApiResponse<Job>>

    @GET("jobs/recommendations.php")
    suspend fun getRecommendedJobs(): Response<ApiResponse<List<Job>>>

    // --- Saved Jobs ---
    @GET("saved_jobs/list.php")
    suspend fun getSavedJobs(): Response<ApiResponse<List<Job>>>

    @POST("saved_jobs/toggle.php")
    suspend fun toggleSaveJob(@Body request: ToggleSaveRequest): Response<ApiResponse<SavedToggleData>>

    @POST("saved_jobs/remove.php")
    suspend fun removeSavedJob(@Body request: ToggleSaveRequest): Response<ApiResponse<SavedToggleData>>

    // --- Applications ---
    @POST("applications/submit.php")
    suspend fun submitApplication(@Body request: ApplyJobRequest): Response<ApiResponse<JobApplication>>

    @GET("applications/list.php")
    suspend fun getApplications(): Response<ApiResponse<List<JobApplication>>>

    // --- Companies ---
    @GET("companies/detail.php")
    suspend fun getCompanyById(@Query("id") id: String): Response<ApiResponse<Company>>

    // --- Placements ---
    @GET("placements/list.php")
    suspend fun getPlacements(): Response<ApiResponse<List<Placement>>>

    @GET("placements/detail.php")
    suspend fun getPlacementById(@Query("id") id: String): Response<ApiResponse<Placement>>

    // --- Resume & AI ---
    @POST("resume/upload.php")
    suspend fun uploadResumeJson(@Body request: UploadResumeJsonRequest): Response<ApiResponse<Resume>>

    @Multipart
    @POST("resume/upload.php")
    suspend fun uploadResumeFile(@Part file: MultipartBody.Part): Response<ApiResponse<Resume>>

    @GET("resume/default.php")
    suspend fun getDefaultResume(): Response<ApiResponse<Resume>>

    @GET("ai/skill_gap.php")
    suspend fun getSkillGapAnalysis(@Query("target_role") targetRole: String? = null): Response<ApiResponse<SkillGapData>>

    @GET("courses/detail.php")
    suspend fun getCourseById(@Query("id") id: String): Response<ApiResponse<Course>>

    // --- Dashboard & Notifications ---
    @GET("dashboard/stats.php")
    suspend fun getDashboardStats(): Response<ApiResponse<DashboardStatsData>>

    @GET("notifications/list.php")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationItem>>>

    @POST("notifications/mark_read.php")
    suspend fun markNotificationRead(@Body request: IdRequest? = null): Response<ApiResponse<Any>>
}
