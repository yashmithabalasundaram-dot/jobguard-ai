package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Resume
import com.jobguardai.jobguardai.data.repository.ResumeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ResumeAnalysisState {
    object Idle : ResumeAnalysisState()
    data class Processing(val progressPercent: Int) : ResumeAnalysisState()
    data class Completed(val resume: Resume) : ResumeAnalysisState()
    data class Error(val message: String) : ResumeAnalysisState()
}

class ResumeViewModel(private val repository: ResumeRepository = ResumeRepository()) : ViewModel() {

    private val _analysisState = MutableStateFlow<ResumeAnalysisState>(ResumeAnalysisState.Idle)
    val analysisState: StateFlow<ResumeAnalysisState> = _analysisState.asStateFlow()

    private val _selectedResume = MutableStateFlow<Resume?>(null)
    val selectedResume: StateFlow<Resume?> = _selectedResume.asStateFlow()

    init {
        loadDefaultResume()
    }

    fun loadDefaultResume() {
        viewModelScope.launch {
            _selectedResume.value = repository.getDefaultResume()
        }
    }

    fun analyzeSelectedResume(fileName: String, uriString: String, rawContent: String = "") {
        viewModelScope.launch {
            _analysisState.value = ResumeAnalysisState.Processing(25)
            delay(300)
            _analysisState.value = ResumeAnalysisState.Processing(65)
            delay(300)
            _analysisState.value = ResumeAnalysisState.Processing(90)

            val content = rawContent.ifBlank { "Professional Resume: $fileName" }

            val res = repository.uploadResume(fileName, content)
            res.onSuccess { uploaded ->
                _selectedResume.value = uploaded
                _analysisState.value = ResumeAnalysisState.Completed(uploaded)
            }.onFailure {
                _analysisState.value = ResumeAnalysisState.Error(it.message ?: "Failed to analyze resume")
            }
        }
    }
}
