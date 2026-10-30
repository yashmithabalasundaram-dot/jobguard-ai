package com.jobguardai.jobguardai.data.remote

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class VerifyEmailRequest(
    val otp_code: String,
    val email: String? = null
)

data class ResendOtpRequest(
    val email: String? = null
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val new_password: String
)

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

data class UpdateProfileRequest(
    val full_name: String? = null,
    val phone: String? = null,
    val headline: String? = null,
    val bio: String? = null,
    val location: String? = null
)

data class UpdatePreferencesRequest(
    val preferred_roles: String? = null,
    val preferred_locations: String? = null,
    val preferred_types: String? = null,
    val expected_salary: String? = null,
    val consent_shared_with_companies: Boolean = true,
    val notifications_enabled: Boolean = true
)

data class SaveEducationRequest(
    val id: String? = null,
    val degree: String,
    val institution: String,
    val field_of_study: String? = null,
    val start_year: String? = null,
    val end_year: String? = null,
    val grade: String? = null
)

data class IdRequest(
    val id: String
)

data class UpdateSkillsRequest(
    val skills: List<String>
)

data class ToggleSaveRequest(
    val job_id: String
)

data class ApplyJobRequest(
    val job_id: String,
    val resume_id: String? = null,
    val cover_note: String? = null
)

data class UploadResumeJsonRequest(
    val file_name: String,
    val parsed_text: String
)

