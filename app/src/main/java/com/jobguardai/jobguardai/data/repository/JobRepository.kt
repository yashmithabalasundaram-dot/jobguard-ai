package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.JobDao
import com.jobguardai.jobguardai.data.model.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class JobRepository(private val jobDao: JobDao? = null) {

    private val demoJobs = listOf(
        Job(
            id = "job_1",
            title = "Senior Android Developer",
            company = "TechCorp Solutions",
            location = "San Francisco, CA (Hybrid)",
            type = "Full-Time",
            salary = "$130k - $160k",
            experience = "3-5 yrs",
            description = "We are seeking a talented Senior Android Developer to lead our mobile team in building next-gen AI driven mobile applications using Jetpack Compose, Kotlin Coroutines, and Clean MVVM Architecture.",
            requirements = listOf(
                "3+ years of native Android development using Kotlin",
                "Strong experience with Jetpack Compose & Material 3",
                "Proficiency in MVVM, Dependency Injection, Room & Coroutines",
                "Experience with CI/CD and automated unit testing"
            ),
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM", "Room", "REST API"),
            isSaved = false,
            matchScore = 94,
            deadline = "15 Oct 2025"
        ),
        Job(
            id = "job_2",
            title = "Mobile UI/UX Engineer",
            company = "InnovateX Labs",
            location = "Remote",
            type = "Remote",
            salary = "$110k - $140k",
            experience = "2-4 yrs",
            description = "InnovateX is looking for a creative UI/UX focused Android Engineer to design and implement slick user interface components with Compose and smooth animation pipelines.",
            requirements = listOf(
                "Proficiency in Jetpack Compose UI & Animation APIs",
                "Eye for pixel-perfect implementation of Figma designs",
                "Experience with StateFlow, ViewModel, and Navigation Compose"
            ),
            skills = listOf("Jetpack Compose", "Figma", "Kotlin", "Material 3", "UI Animations"),
            isSaved = true,
            matchScore = 88,
            deadline = "20 Oct 2025"
        ),
        Job(
            id = "job_3",
            title = "AI Mobile Integration Specialist",
            company = "CyberGenesis Inc.",
            location = "New York, NY",
            type = "Contract",
            salary = "$75 - $90 / hr",
            experience = "4+ yrs",
            description = "Join CyberGenesis to embed lightweight ML models and On-Device Generative AI features into native Android clients.",
            requirements = listOf(
                "Experience integrating TensorFlow Lite or On-Device AI models in Android",
                "Deep understanding of async programming with Kotlin Coroutines & Flow"
            ),
            skills = listOf("Kotlin", "On-Device AI", "Coroutines", "TensorFlow Lite", "Retrofit"),
            isSaved = false,
            matchScore = 82,
            deadline = "28 Oct 2025"
        ),
        Job(
            id = "job_4",
            title = "Urgent Data Entry Specialist (High Salary!)",
            company = "Global Wealth Systems LLC",
            location = "Work from Home",
            type = "Part-Time",
            salary = "$5,000 / week",
            experience = "No Experience Required",
            description = "Earn big money working just 2 hours a day from home! Pay registration fee of $50 upfront to get started immediately. No interview required.",
            requirements = listOf(
                "Basic typing skills",
                "Upfront payment required for starter kit"
            ),
            skills = listOf("Data Entry", "Typing"),
            isSaved = false,
            isScamFlagged = true,
            scamReason = "SUSPICIOUS: Requires upfront payment ($50), unrealistically high pay ($5,000/wk) for entry level, and no interview required.",
            matchScore = 20,
            deadline = "Immediate"
        )
    )

    fun getJobs(): Flow<List<Job>> {
        return jobDao?.getAllJobs() ?: flowOf(demoJobs)
    }

    fun getSavedJobs(): Flow<List<Job>> {
        return jobDao?.getSavedJobs() ?: flowOf(demoJobs.filter { it.isSaved })
    }

    suspend fun getJobById(id: String): Job? {
        return jobDao?.getJobById(id) ?: demoJobs.find { it.id == id }
    }

    suspend fun toggleSaveJob(id: String, currentSavedState: Boolean) {
        jobDao?.updateSavedState(id, !currentSavedState)
    }
}
