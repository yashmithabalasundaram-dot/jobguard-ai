package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import com.jobguardai.jobguardai.ai.SkillGapAnalyzer
import com.jobguardai.jobguardai.ai.SkillGapResult
import com.jobguardai.jobguardai.data.model.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CareerCoachViewModel : ViewModel() {

    private val _skillGapResult = MutableStateFlow<SkillGapResult?>(null)
    val skillGapResult: StateFlow<SkillGapResult?> = _skillGapResult.asStateFlow()

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    fun analyzeSkillGaps(userSkills: List<String>, requiredSkills: List<String>) {
        val result = SkillGapAnalyzer.analyze(userSkills, requiredSkills)
        _skillGapResult.value = result
    }

    fun selectCourse(course: Course) {
        _selectedCourse.value = course
    }
}
