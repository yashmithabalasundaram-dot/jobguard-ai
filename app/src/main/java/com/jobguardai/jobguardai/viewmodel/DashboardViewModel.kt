package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.remote.DashboardStatsData
import com.jobguardai.jobguardai.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val stats: DashboardStatsData,
        val recommendedJobs: List<Job>
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            val result = repository.getDashboardStats()
            result.onSuccess { data ->
                val mappedJobs = data.recommendedJobs.map { dto ->
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
                _uiState.value = DashboardUiState.Success(data, mappedJobs)
            }.onFailure {
                _uiState.value = DashboardUiState.Error(it.message ?: "Failed to load dashboard")
            }
        }
    }
}

