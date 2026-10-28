package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.model.JobSeeker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _currentUser = MutableStateFlow<JobSeeker?>(
        JobSeeker(
            id = "user_1",
            fullName = "Alex Morgan",
            email = "alex.morgan@example.com",
            phone = "+1 (555) 019-2834",
            bio = "Enthusiastic Software Engineer passionate about AI and mobile design.",
            headline = "Android Developer | Kotlin & Compose Enthusiast",
            location = "San Francisco, CA",
            skillsList = listOf("Kotlin", "Jetpack Compose", "Android SDK", "MVVM", "Room", "Coroutines"),
            preferredRoles = listOf("Android Developer", "Mobile Engineer", "Kotlin Developer"),
            preferredLocations = listOf("San Francisco, CA", "Remote", "New York, NY")
        )
    )
    val currentUser: StateFlow<JobSeeker?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    suspend fun login(email: String, password: String): Result<JobSeeker> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }
        _isLoggedIn.value = true
        val user = _currentUser.value ?: JobSeeker(email = email)
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun register(fullName: String, email: String, phone: String, password: String): Result<JobSeeker> {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please fill in all required fields"))
        }
        val user = JobSeeker(
            id = "user_${System.currentTimeMillis()}",
            fullName = fullName,
            email = email,
            phone = phone
        )
        _currentUser.value = user
        _isLoggedIn.value = true
        return Result.success(user)
    }

    suspend fun updateProfile(updatedUser: JobSeeker) {
        _currentUser.value = updatedUser
    }

    suspend fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
    }
}
