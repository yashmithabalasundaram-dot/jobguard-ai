package com.jobguardai.jobguardai.ai

import com.jobguardai.jobguardai.data.model.Course

data class SkillGapResult(
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val readinessPercentage: Int,
    val recommendedCourses: List<Course>
)

object SkillGapAnalyzer {
    fun analyze(userSkills: List<String>, requiredSkills: List<String>): SkillGapResult {
        val lowerUserSkills = userSkills.map { it.lowercase() }
        val matched = mutableListOf<String>()
        val missing = mutableListOf<String>()

        requiredSkills.forEach { skill ->
            if (lowerUserSkills.contains(skill.lowercase())) {
                matched.add(skill)
            } else {
                missing.add(skill)
            }
        }

        val total = requiredSkills.size.coerceAtLeast(1)
        val readinessPercent = ((matched.size.toFloat() / total.toFloat()) * 100).toInt().coerceIn(20, 100)

        val courses = missing.mapIndexed { index, skill ->
            Course(
                id = "course_$index",
                title = "Mastering $skill for Modern Android",
                provider = "JobGuard AI Academy",
                duration = "4 Hours",
                level = "Intermediate",
                skillCategory = skill,
                progressPercent = 0,
                description = "Comprehensive practical course covering $skill with hands-on Kotlin exercises."
            )
        }

        return SkillGapResult(
            matchedSkills = matched,
            missingSkills = missing,
            readinessPercentage = readinessPercent,
            recommendedCourses = courses
        )
    }
}
