package com.jobguardai.jobguardai.ai

import com.jobguardai.jobguardai.data.model.Job

object JobRecommendationEngine {
    fun calculateMatchScore(userSkills: List<String>, job: Job): Int {
        if (userSkills.isEmpty()) return job.matchScore
        val lowerUserSkills = userSkills.map { it.lowercase() }
        var matchCount = 0

        job.skills.forEach { skill ->
            if (lowerUserSkills.contains(skill.lowercase())) {
                matchCount++
            }
        }

        if (job.skills.isEmpty()) return 80

        val score = ((matchCount.toFloat() / job.skills.size.toFloat()) * 100).toInt()
        return score.coerceIn(40, 98)
    }
}
