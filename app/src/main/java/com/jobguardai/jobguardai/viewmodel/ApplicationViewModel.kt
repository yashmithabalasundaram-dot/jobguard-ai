package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Job
import com.jobguardai.jobguardai.data.model.JobApplication
import com.jobguardai.jobguardai.data.repository.ApplicationRepository
import com.jobguardai.jobguardai.domain.usecase.ApplyForJobUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ApplicationState {
    object Idle : ApplicationState()
    object Submitting : ApplicationState()
    data class Submitted(val application: JobApplication) : ApplicationState()
    data class Error(val message: String) : ApplicationState()
}

class ApplicationViewModel(private val repository: ApplicationRepository = ApplicationRepository()) : ViewModel() {

    private val applyUseCase = ApplyForJobUseCase(repository)

    val applications: StateFlow<List<JobApplication>> = repository.getApplications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _submissionState = MutableStateFlow<ApplicationState>(ApplicationState.Idle)
    val submissionState: StateFlow<ApplicationState> = _submissionState.asStateFlow()

    fun submitApplication(job: Job, resumeId: String, coverNote: String) {
        viewModelScope.launch {
            _submissionState.value = ApplicationState.Submitting
            val result = applyUseCase(job, resumeId, coverNote)
            result.onSuccess {
                _submissionState.value = ApplicationState.Submitted(it)
            }.onFailure {
                _submissionState.value = ApplicationState.Error(it.message ?: "Submission failed")
            }
        }
    }
}
