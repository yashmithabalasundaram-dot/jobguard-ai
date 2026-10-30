package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.ResumeDao
import com.jobguardai.jobguardai.data.model.Resume
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import com.jobguardai.jobguardai.data.remote.UploadResumeJsonRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ResumeRepository(
    private val resumeDao: ResumeDao? = null,
    private val apiService: ApiService = RetrofitClient.getApiService()
) {

    fun getResumes(): Flow<List<Resume>> {
        return resumeDao?.getAllResumes() ?: flowOf(emptyList())
    }

    suspend fun getDefaultResume(): Resume? {
        try {
            val response = apiService.getDefaultResume()
            if (response.isSuccessful && response.body()?.success == true) {
                val r = response.body()!!.data
                if (r != null) return r
            }
        } catch (e: Exception) {
            // Offline
        }
        return resumeDao?.getDefaultResume()
    }

    suspend fun uploadResume(fileName: String, content: String): Result<Resume> {
        return try {
            val res = apiService.uploadResumeJson(UploadResumeJsonRequest(fileName, content))
            if (res.isSuccessful && res.body()?.success == true) {
                val resume = res.body()!!.data
                if (resume != null) {
                    resumeDao?.insertResume(resume)
                    Result.success(resume)
                } else {
                    Result.failure(Exception("Failed to parse uploaded resume"))
                }
            } else {
                Result.failure(Exception(res.body()?.message ?: "Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to upload resume to backend"))
        }
    }

    suspend fun saveResume(resume: Resume) {
        resumeDao?.insertResume(resume)
    }
}
