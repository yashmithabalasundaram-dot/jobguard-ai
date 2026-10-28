package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.JobApplicationDao
import com.jobguardai.jobguardai.data.model.JobApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ApplicationRepository(private val applicationDao: JobApplicationDao? = null) {

    private val demoApplications = listOf(
        JobApplication(
            id = "app_101",
            jobId = "job_1",
            jobTitle = "Senior Android Developer",
            companyName = "TechCorp Solutions",
            appliedDate = "14 Oct 2025",
            status = "Under Review",
            resumeId = "res_1",
            coverNote = "I am eager to contribute my expertise in Jetpack Compose and Kotlin Coroutines to TechCorp."
        ),
        JobApplication(
            id = "app_102",
            jobId = "job_2",
            jobTitle = "Mobile UI/UX Engineer",
            companyName = "InnovateX Labs",
            appliedDate = "10 Oct 2025",
            status = "Interview Scheduled",
            resumeId = "res_1",
            coverNote = "My strong background in Material 3 design and UI engineering aligns perfectly with this role."
        )
    )

    fun getApplications(): Flow<List<JobApplication>> {
        return applicationDao?.getAllApplications() ?: flowOf(demoApplications)
    }

    suspend fun submitApplication(application: JobApplication) {
        applicationDao?.insertApplication(application)
    }
}
