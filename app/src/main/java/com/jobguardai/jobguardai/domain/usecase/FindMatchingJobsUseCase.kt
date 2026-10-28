package com.jobguardai.jobguardai.domain.usecase

import com.jobguardai.jobguardai.ai.JobRecommendationEngine
import com.jobguardai.jobguardai.data.model.Job

class FindMatchingJobsUseCase {
    operator fun invoke(userSkills: List<String>, jobs: List<Job>): List<Job> {
        return jobs.map { job ->
            val updatedMatchScore = JobRecommendationEngine.calculateMatchScore(userSkills, job)
            job.copy(matchScore = updatedMatchScore)
        }.sortedByDescending { it.matchScore }
    }
}
