package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.ai.SkillGapAnalyzer
import com.jobguardai.jobguardai.ai.SkillGapResult
import com.jobguardai.jobguardai.data.model.Course
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CareerCoachViewModel(
    private val apiService: ApiService = RetrofitClient.getApiService()
) : ViewModel() {

    private val _skillGapResult = MutableStateFlow<SkillGapResult?>(null)
    val skillGapResult: StateFlow<SkillGapResult?> = _skillGapResult.asStateFlow()

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    fun analyzeSkillGaps(userSkills: List<String>, requiredSkills: List<String>, targetRole: String = "Senior Android Developer") {
        viewModelScope.launch {
            try {
                val res = apiService.getSkillGapAnalysis(targetRole)
                if (res.isSuccessful && res.body()?.success == true) {
                    val data = res.body()!!.data
                    if (data != null) {
                        val mappedCourses = data.recommendedCourses.map { dto ->
                            Course(
                                id = dto.id,
                                title = dto.title,
                                provider = dto.provider,
                                duration = dto.duration,
                                level = dto.level,
                                skillCategory = dto.skillCategory,
                                progressPercent = dto.progressPercent,
                                url = dto.url,
                                description = dto.description,
                                modules = dto.modules,
                                isCompleted = dto.isCompleted
                            )
                        }
                        _skillGapResult.value = SkillGapResult(
                            matchedSkills = data.matchedSkills,
                            missingSkills = data.missingSkills,
                            readinessPercentage = data.readinessPercentage,
                            recommendedCourses = mappedCourses
                        )
                        return@launch
                    }
                }
            } catch (e: Exception) {
                // Fallback to local analyzer
            }
            val result = SkillGapAnalyzer.analyze(userSkills, requiredSkills)
            _skillGapResult.value = result
        }
    }

    fun selectCourse(course: Course) {
        _selectedCourse.value = course
    }

    fun loadCourseById(courseId: String) {
        viewModelScope.launch {
            try {
                val res = apiService.getCourseById(courseId)
                if (res.isSuccessful && res.body()?.success == true) {
                    val cr = res.body()!!.data
                    if (cr != null) {
                        _selectedCourse.value = cr
                        return@launch
                    }
                }
            } catch (e: Exception) {
                // Fallback
            }
            if (_selectedCourse.value == null) {
                _selectedCourse.value = Course(
                    id = courseId,
                    title = "Mastering Jetpack Compose UI",
                    provider = "JobGuard AI Academy",
                    duration = "6 Hours",
                    level = "Intermediate",
                    skillCategory = "Jetpack Compose",
                    progressPercent = 35,
                    description = "Comprehensive practical course covering Jetpack Compose with hands-on Kotlin exercises."
                )
            }
        }
    }
}
