package com.jobguardai.jobguardai.domain.usecase

import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.model.JobApplication
import com.jobguardai.jobguardai.data.repository.ApplicationRepository

class ApplyForJobUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(job: Job, resumeId: String, coverNote: String): Result<JobApplication> {
        val application = JobApplication(
            id = "app_${System.currentTimeMillis()}",
            jobId = job.id,
            jobTitle = job.title,
            companyName = job.company,
            appliedDate = "Today",
            status = "Submitted",
            resumeId = resumeId,
            coverNote = coverNote,
            isDemo = true
        )
        repository.submitApplication(application)
        return Result.success(application)
    }
}
