package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.ResumeDao
import com.jobguardai.jobguardai.data.model.Resume
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ResumeRepository(private val resumeDao: ResumeDao? = null) {

    private val sampleResume = Resume(
        id = "res_1",
        fileName = "Alex_Morgan_Resume_2025.pdf",
        fileUri = "content://com.android.providers.downloads.documents/document/resume_1.pdf",
        uploadDate = "12 Oct 2025",
        fileSize = "1.4 MB",
        parsedText = "Alex Morgan - Senior Android Engineer. Proficient in Kotlin, Jetpack Compose, MVVM, Room, Coroutines, Git, CI/CD, and REST APIs.",
        extractedSkills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM Architecture", "Room DB", "REST APIs", "Git"),
        extractedEducation = listOf("B.S. Computer Science - Stanford University (2020-2024)"),
        extractedExperience = listOf("Android Developer Intern - TechCorp (2023-2024)", "Mobile Project Lead - Campus App (2022-2023)"),
        missingSections = listOf("Certifications", "Quantified Achievements in Past Work"),
        improvementSuggestions = listOf(
            "Add measurable metrics to project outcomes (e.g., 'Improved app speed by 35%')",
            "Include links to live GitHub repositories or Google Play apps",
            "Highlight experience with modern Kotlin Flow and dependency injection (Hilt)"
        ),
        overallScore = 88,
        isDefault = true
    )

    fun getResumes(): Flow<List<Resume>> {
        return resumeDao?.getAllResumes() ?: flowOf(listOf(sampleResume))
    }

    suspend fun getDefaultResume(): Resume {
        return resumeDao?.getDefaultResume() ?: sampleResume
    }

    suspend fun saveResume(resume: Resume) {
        resumeDao?.insertResume(resume)
    }
}
