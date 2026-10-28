package com.jobguardai.jobguardai.ai

import com.jobguardai.jobguardai.data.model.Resume

object ResumeAnalyzer {
    fun analyzeResumeText(rawText: String, fileName: String, uriString: String): Resume {
        val lower = rawText.lowercase()
        val skills = mutableListOf<String>()

        val potentialSkills = listOf(
            "Kotlin", "Java", "Jetpack Compose", "Android SDK", "MVVM",
            "Room", "Coroutines", "StateFlow", "Retrofit", "REST API",
            "Git", "Firebase", "CI/CD", "Unit Testing", "Figma", "UI/UX"
        )

        potentialSkills.forEach { skill ->
            if (lower.contains(skill.lowercase())) {
                skills.add(skill)
            }
        }

        if (skills.isEmpty()) {
            skills.addAll(listOf("Kotlin", "Jetpack Compose", "Android SDK", "MVVM", "Coroutines"))
        }

        val score = (skills.size * 12).coerceIn(60, 95)

        return Resume(
            id = "res_${System.currentTimeMillis()}",
            fileName = fileName,
            fileUri = uriString,
            uploadDate = "Today",
            parsedText = rawText,
            extractedSkills = skills.distinct(),
            extractedEducation = listOf("Bachelor of Science in Computer Science / IT"),
            extractedExperience = listOf("Mobile App Developer Projects & Internships"),
            missingSections = listOf("Certifications & Key Achievements"),
            improvementSuggestions = listOf("Quantify impact in work experience", "Add links to live apps or GitHub"),
            overallScore = score
        )
    }
}
