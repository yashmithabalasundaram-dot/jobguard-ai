package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.ai.ScamDetectionEngine
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JobViewModel(private val repository: JobRepository = JobRepository()) : ViewModel() {

    val allJobs: StateFlow<List<Job>> = repository.getJobs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val savedJobs: StateFlow<List<Job>> = repository.getSavedJobs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterRole = MutableStateFlow("All")
    val filterRole: StateFlow<String> = _filterRole.asStateFlow()

    val filteredJobs: StateFlow<List<Job>> = combine(
        allJobs,
        _searchQuery,
        _filterRole
    ) { jobs, query, role ->
        jobs.filter { job ->
            val matchesQuery = query.isBlank() ||
                    job.title.contains(query, ignoreCase = true) ||
                    job.company.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true) ||
                    job.skills.any { it.contains(query, ignoreCase = true) }

            val matchesRole = role == "All" || job.type.equals(role, ignoreCase = true)

            matchesQuery && matchesRole
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectJob(jobId: String) {
        viewModelScope.launch {
            _selectedJob.value = repository.getJobById(jobId)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterRole(role: String) {
        _filterRole.value = role
    }

    fun toggleSave(job: Job) {
        viewModelScope.launch {
            repository.toggleSaveJob(job.id, job.isSaved)
        }
    }

    fun getSafetyReport(job: Job) = ScamDetectionEngine.inspectJobPosting(job)
}
