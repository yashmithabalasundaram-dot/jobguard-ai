package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.ai.ResumeAnalyzer
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

    private fun loadDefaultResume() {
        viewModelScope.launch {
            _selectedResume.value = repository.getDefaultResume()
        }
    }

    fun analyzeSelectedResume(fileName: String, uriString: String, rawContent: String = "") {
        viewModelScope.launch {
            _analysisState.value = ResumeAnalysisState.Processing(10)
            delay(600)
            _analysisState.value = ResumeAnalysisState.Processing(45)
            delay(600)
            _analysisState.value = ResumeAnalysisState.Processing(85)
            delay(500)

            val parsedContent = if (rawContent.isBlank()) {
                "Resume of candidate with skills in Kotlin, Jetpack Compose, MVVM, Room, REST APIs, Git, Unit Testing, and UI Design."
            } else rawContent

            val result = ResumeAnalyzer.analyzeResumeText(parsedContent, fileName, uriString)
            repository.saveResume(result)
            _selectedResume.value = result
            _analysisState.value = ResumeAnalysisState.Completed(result)
        }
    }
}
