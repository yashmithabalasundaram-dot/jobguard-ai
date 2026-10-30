package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.SessionManager
import com.jobguardai.jobguardai.data.model.Education
import com.jobguardai.jobguardai.data.model.JobSeeker
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.ForgotPasswordRequest
import com.jobguardai.jobguardai.data.remote.LoginRequest
import com.jobguardai.jobguardai.data.remote.RegisterRequest
import com.jobguardai.jobguardai.data.remote.ResendOtpRequest
import com.jobguardai.jobguardai.data.remote.ResetPasswordRequest
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import com.jobguardai.jobguardai.data.remote.SaveEducationRequest
import com.jobguardai.jobguardai.data.remote.ChangePasswordRequest
import com.jobguardai.jobguardai.data.remote.UpdatePreferencesRequest
import com.jobguardai.jobguardai.data.remote.UpdateProfileRequest
import com.jobguardai.jobguardai.data.remote.UpdateSkillsRequest
import com.jobguardai.jobguardai.data.remote.VerifyEmailRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val apiService: ApiService = RetrofitClient.getApiService(),
    private val sessionManager: SessionManager? = RetrofitClient.getSessionManager()
) {
    private val _currentUser = MutableStateFlow<JobSeeker?>(
        if (sessionManager?.isLoggedIn() == true && !sessionManager.getUserId().isNullOrBlank()) {
            JobSeeker(
                id = sessionManager.getUserId() ?: "",
                fullName = sessionManager.getUserName(),
                email = sessionManager.getUserEmail(),
                phone = sessionManager.getUserPhone(),
                isVerified = sessionManager.isUserVerified()
            )
        } else {
            null
        }
    )
    val currentUser: StateFlow<JobSeeker?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(sessionManager?.isLoggedIn() ?: false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _lastGeneratedOtp = MutableStateFlow<String?>(null)
    val lastGeneratedOtp: StateFlow<String?> = _lastGeneratedOtp.asStateFlow()

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<JobSeeker> {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Please fill in all required fields"))
        }

        return try {
            val response = apiService.register(
                RegisterRequest(full_name = name, email = email, phone = phone, password = pass)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                val token = data?.token ?: ""
                val uDto = data?.user

                val userId = uDto?.id ?: ""
                val userName = uDto?.full_name ?: name
                val userEmail = uDto?.email ?: email
                val userPhone = uDto?.phone ?: phone
                val isVerified = uDto?.is_verified ?: false

                sessionManager?.saveAuthToken(token)
                sessionManager?.saveUser(
                    id = userId,
                    name = userName,
                    email = userEmail,
                    phone = userPhone,
                    isVerified = isVerified
                )

                _lastGeneratedOtp.value = data?.otp_code

                val user = JobSeeker(
                    id = userId,
                    fullName = userName,
                    email = userEmail,
                    phone = userPhone,
                    isVerified = isVerified,
                    profileCompletionPct = 20
                )
                _currentUser.value = user
                _isLoggedIn.value = true
                Result.success(user)
            } else {
                val errMsg = response.body()?.message ?: "Registration failed. Please try again."
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error during registration. Ensure backend is running."))
        }
    }

    suspend fun login(email: String, pass: String): Result<JobSeeker> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }

        return try {
            val response = apiService.login(LoginRequest(email = email, password = pass))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                val token = data?.token ?: ""
                val uDto = data?.user

                val userId = uDto?.id ?: ""
                val userName = uDto?.full_name ?: ""
                val userEmail = uDto?.email ?: email
                val userPhone = uDto?.phone ?: ""
                val isVerified = uDto?.is_verified ?: true

                sessionManager?.saveAuthToken(token)
                sessionManager?.saveUser(
                    id = userId,
                    name = userName,
                    email = userEmail,
                    phone = userPhone,
                    isVerified = isVerified
                )

                val user = JobSeeker(
                    id = userId,
                    fullName = userName,
                    email = userEmail,
                    phone = userPhone,
                    isVerified = isVerified,
                    headline = uDto?.headline ?: "",
                    bio = uDto?.bio ?: "",
                    location = uDto?.location ?: "",
                    profileCompletionPct = uDto?.profile_completion_pct ?: 20
                )
                _currentUser.value = user
                _isLoggedIn.value = true
                Result.success(user)
            } else {
                val msg = response.body()?.message ?: "Invalid email or password"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Network error connecting to backend."))
        }
    }

    suspend fun verifyEmail(otpCode: String, email: String? = null): Result<Boolean> {
        return try {
            val targetEmail = email ?: _currentUser.value?.email ?: sessionManager?.getUserEmail()
            val response = apiService.verifyEmail(VerifyEmailRequest(otp_code = otpCode, email = targetEmail))
            if (response.isSuccessful && response.body()?.success == true) {
                val curr = _currentUser.value
                if (curr != null) {
                    _currentUser.value = curr.copy(isVerified = true)
                }
                sessionManager?.saveUser(
                    id = sessionManager.getUserId() ?: "",
                    name = sessionManager.getUserName(),
                    email = sessionManager.getUserEmail(),
                    phone = sessionManager.getUserPhone(),
                    isVerified = true
                )
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Invalid OTP code"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Verification failed"))
        }
    }

    suspend fun resendOtp(email: String? = null): Result<String> {
        return try {
            val targetEmail = email ?: _currentUser.value?.email ?: sessionManager?.getUserEmail()
            val response = apiService.resendOtp(ResendOtpRequest(email = targetEmail))
            if (response.isSuccessful && response.body()?.success == true) {
                val newOtp = response.body()?.data?.get("otp_code") ?: "1234"
                _lastGeneratedOtp.value = newOtp
                Result.success(newOtp)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Could not resend OTP"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to resend OTP"))
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful && response.body()?.success == true) {
                val token = response.body()?.data?.get("reset_token") ?: ""
                Result.success(token)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to generate reset link"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to send reset link"))
        }
    }

    suspend fun resetPassword(token: String, newPass: String): Result<Boolean> {
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(token, newPass))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to reset password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to reset password"))
        }
    }

    suspend fun changePassword(currentPass: String, newPass: String): Result<Boolean> {
        return try {
            val response = apiService.changePassword(ChangePasswordRequest(currentPass, newPass))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to change password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to change password"))
        }
    }

    suspend fun refreshUserProfile() {
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                val u = data?.user
                val p = data?.profile

                val updated = JobSeeker(
                    id = u?.id ?: _currentUser.value?.id ?: "",
                    fullName = u?.full_name ?: _currentUser.value?.fullName ?: "",
                    email = u?.email ?: _currentUser.value?.email ?: "",
                    phone = u?.phone ?: _currentUser.value?.phone ?: "",
                    isVerified = u?.is_verified ?: true,
                    headline = p?.headline ?: _currentUser.value?.headline ?: "",
                    bio = p?.bio ?: _currentUser.value?.bio ?: "",
                    location = p?.location ?: _currentUser.value?.location ?: "",
                    skillsList = data?.skills ?: _currentUser.value?.skillsList ?: emptyList(),
                    preferredRoles = data?.preferences?.preferred_roles?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
                    preferredLocations = data?.preferences?.preferred_locations?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
                    profileCompletionPct = p?.profile_completion_pct ?: 20
                )
                _currentUser.value = updated
                sessionManager?.saveUser(
                    id = updated.id,
                    name = updated.fullName,
                    email = updated.email,
                    phone = updated.phone,
                    isVerified = updated.isVerified
                )
            }
        } catch (e: Exception) {
            // Keep current value if offline
        }
    }

    suspend fun updateProfile(updatedUser: JobSeeker) {
        _currentUser.value = updatedUser
        try {
            apiService.updateProfile(
                UpdateProfileRequest(
                    full_name = updatedUser.fullName,
                    phone = updatedUser.phone,
                    headline = updatedUser.headline,
                    bio = updatedUser.bio,
                    location = updatedUser.location
                )
            )
            refreshUserProfile()
        } catch (e: Exception) {
            // Updated in memory
        }
    }

    suspend fun saveEducation(edu: Education): Result<Boolean> {
        return try {
            val req = SaveEducationRequest(
                id = edu.id,
                degree = edu.degree,
                institution = edu.institution,
                field_of_study = edu.fieldOfStudy,
                start_year = edu.startYear,
                end_year = edu.endYear,
                grade = edu.grade
            )
            val res = apiService.saveEducation(req)
            if (res.isSuccessful && res.body()?.success == true) {
                refreshUserProfile()
                Result.success(true)
            } else {
                Result.failure(Exception(res.body()?.message ?: "Failed to save education"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to save education"))
        }
    }

    suspend fun updateSkills(skills: List<String>): Result<Boolean> {
        return try {
            val res = apiService.updateSkills(UpdateSkillsRequest(skills))
            if (res.isSuccessful && res.body()?.success == true) {
                val current = _currentUser.value
                if (current != null) {
                    _currentUser.value = current.copy(skillsList = skills)
                }
                refreshUserProfile()
                Result.success(true)
            } else {
                Result.failure(Exception(res.body()?.message ?: "Failed to update skills"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to update skills"))
        }
    }

    suspend fun updatePreferences(roles: List<String>, locations: List<String>): Result<Boolean> {
        return try {
            val req = UpdatePreferencesRequest(
                preferred_roles = roles.joinToString(", "),
                preferred_locations = locations.joinToString(", ")
            )
            val res = apiService.updatePreferences(req)
            if (res.isSuccessful && res.body()?.success == true) {
                val current = _currentUser.value
                if (current != null) {
                    _currentUser.value = current.copy(preferredRoles = roles, preferredLocations = locations)
                }
                refreshUserProfile()
                Result.success(true)
            } else {
                Result.failure(Exception(res.body()?.message ?: "Failed to save preferences"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to save preferences"))
        }
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (e: Exception) {
            // Ignore
        }
        sessionManager?.clearSession()
        _isLoggedIn.value = false
        _currentUser.value = null
    }
}
