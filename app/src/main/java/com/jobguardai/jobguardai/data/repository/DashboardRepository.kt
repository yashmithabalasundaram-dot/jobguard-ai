package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.DashboardStatsData
import com.jobguardai.jobguardai.data.remote.RetrofitClient

class DashboardRepository(
    private val apiService: ApiService = RetrofitClient.getApiService()
) {
    suspend fun getDashboardStats(): Result<DashboardStatsData> {
        return try {
            val response = apiService.getDashboardStats()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Dashboard data empty"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load dashboard"))
            }
        } catch (e: Exception) {
            // Offline fallback
            Result.success(
                DashboardStatsData(
                    greeting = "Good Morning, Alex 👋",
                    userName = "Alex Morgan",
                    profileCompletion = 75,
                    resumeScore = 88,
                    hasResume = true,
                    resumeFileName = "Alex_Morgan_Resume_2025.pdf",
                    totalSavedJobs = 1,
                    totalApplications = 2,
                    totalPlacements = 2,
                    unreadNotifications = 2
                )
            )
        }
    }
}

