package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.JobApplicationDao
import com.jobguardai.jobguardai.data.model.JobApplication
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.ApplyJobRequest
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class ApplicationRepository(
    private val applicationDao: JobApplicationDao? = null,
    private val apiService: ApiService = RetrofitClient.getApiService()
) {

    private val _applicationsList = MutableStateFlow<List<JobApplication>>(emptyList())
    val applicationsList = _applicationsList.asStateFlow()

    fun getApplications(): Flow<List<JobApplication>> = flow {
        try {
            val response = apiService.getApplications()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data ?: emptyList()
                _applicationsList.value = data
                emit(data)
                return@flow
            }
        } catch (e: Exception) {
            // Error connecting to backend
        }
        emit(_applicationsList.value)
    }

    suspend fun submitApplication(application: JobApplication): Result<JobApplication> {
        return try {
            val req = ApplyJobRequest(
                job_id = application.jobId,
                resume_id = application.resumeId,
                cover_note = application.coverNote
            )
            val response = apiService.submitApplication(req)
            if (response.isSuccessful && response.body()?.success == true) {
                val created = response.body()!!.data ?: application
                _applicationsList.value = listOf(created) + _applicationsList.value.filter { it.jobId != application.jobId }
                applicationDao?.insertApplication(created)
                Result.success(created)
            } else {
                val msg = response.body()?.message ?: "Application submission failed"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to submit application to server"))
        }
    }
}
