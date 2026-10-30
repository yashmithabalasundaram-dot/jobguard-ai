package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.local.JobDao
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import com.jobguardai.jobguardai.data.remote.ToggleSaveRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class JobRepository(
    private val jobDao: JobDao? = null,
    private val apiService: ApiService = RetrofitClient.getApiService()
) {

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

    private val _liveJobs = MutableStateFlow<List<Job>>(emptyList())
    val liveJobs = _liveJobs.asStateFlow()

    private val _savedJobsList = MutableStateFlow<List<Job>>(emptyList())
    val savedJobsList = _savedJobsList.asStateFlow()

    fun getJobs(): Flow<List<Job>> = flow {
        try {
            val response = apiService.searchJobs(limit = 50)
            if (response.isSuccessful && response.body()?.success == true) {
                val dtoList = response.body()!!.data?.jobs ?: emptyList()
                val mapped = dtoList.map { dto ->
                    Job(
                        id = dto.id,
                        title = dto.title,
                        company = dto.company,
                        companyLogo = dto.companyLogo,
                        location = dto.location,
                        type = dto.type,
                        salary = dto.salary,
                        experience = dto.experience,
                        description = dto.description,
                        requirements = dto.requirements,
                        skills = dto.skills,
                        isSaved = dto.isSaved,
                        isScamFlagged = dto.isScamFlagged,
                        scamReason = dto.scamReason,
                        matchScore = dto.matchScore,
                        deadline = dto.deadline,
                        externalUrl = dto.externalUrl
                    )
                }
                _liveJobs.value = mapped
                emit(mapped)
                return@flow
            }
        } catch (e: Exception) {
            // Network fallback
        }
        emit(_liveJobs.value)
    }

    suspend fun searchJobs(query: String? = null, role: String? = null): List<Job> {
        return try {
            val response = apiService.searchJobs(query = query, type = if (role == "All") null else role)
            if (response.isSuccessful && response.body()?.success == true) {
                val dtoList = response.body()!!.data?.jobs ?: emptyList()
                dtoList.map { dto ->
                    Job(
                        id = dto.id,
                        title = dto.title,
                        company = dto.company,
                        companyLogo = dto.companyLogo,
                        location = dto.location,
                        type = dto.type,
                        salary = dto.salary,
                        experience = dto.experience,
                        description = dto.description,
                        requirements = dto.requirements,
                        skills = dto.skills,
                        isSaved = dto.isSaved,
                        isScamFlagged = dto.isScamFlagged,
                        scamReason = dto.scamReason,
                        matchScore = dto.matchScore,
                        deadline = dto.deadline,
                        externalUrl = dto.externalUrl
                    )
                }
            } else {
                filterLocalJobs(query, role)
            }
        } catch (e: Exception) {
            filterLocalJobs(query, role)
        }
    }

    private fun filterLocalJobs(query: String?, role: String?): List<Job> {
        return _liveJobs.value.filter { job ->
            val matchesQuery = query.isNullOrBlank() ||
                    job.title.contains(query, ignoreCase = true) ||
                    job.effectiveCompany.contains(query, ignoreCase = true) ||
                    job.description.contains(query, ignoreCase = true)
            val matchesRole = role == null || role == "All" || job.type.contains(role, ignoreCase = true)
            matchesQuery && matchesRole
        }
    }

    fun getSavedJobs(): Flow<List<Job>> = flow {
        try {
            val response = apiService.getSavedJobs()
            if (response.isSuccessful && response.body()?.success == true) {
                val jobs = response.body()!!.data ?: emptyList()
                _savedJobsList.value = jobs
                emit(jobs)
                return@flow
            }
        } catch (e: Exception) {
            // Fallback
        }
        emit(_savedJobsList.value)
    }

    suspend fun getJobById(id: String): Job? {
        try {
            val response = apiService.getJobById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                return response.body()!!.data
            }
        } catch (e: Exception) {
            // Fallback
        }
        return _liveJobs.value.find { it.id == id } ?: demoJobs.find { it.id == id }
    }

    suspend fun toggleSaveJob(jobId: String, currentSavedState: Boolean) {
        val newSaved = !currentSavedState
        // Optimistic UI update
        _liveJobs.value = _liveJobs.value.map {
            if (it.id == jobId) it.copy(isSaved = newSaved) else it
        }

        if (newSaved) {
            val jobToAdd = _liveJobs.value.find { it.id == jobId }
            if (jobToAdd != null && _savedJobsList.value.none { it.id == jobId }) {
                _savedJobsList.value = _savedJobsList.value + jobToAdd.copy(isSaved = true)
            }
        } else {
            _savedJobsList.value = _savedJobsList.value.filter { it.id != jobId }
        }

        try {
            apiService.toggleSaveJob(ToggleSaveRequest(jobId))
        } catch (e: Exception) {
            // Local fallback already handled
        }
    }
}
