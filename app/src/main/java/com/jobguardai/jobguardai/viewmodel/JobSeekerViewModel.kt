package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.Education
import com.jobguardai.jobguardai.data.model.JobSeeker
import com.jobguardai.jobguardai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobSeekerViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    val userProfile: StateFlow<JobSeeker?> = repository.currentUser

    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    init {
        refreshProfile()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            repository.refreshUserProfile()
        }
    }

    fun updatePersonalDetails(name: String, email: String, phone: String, bio: String, headline: String, location: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: JobSeeker()
            val updated = current.copy(
                fullName = name,
                email = email,
                phone = phone,
                bio = bio,
                headline = headline,
                location = location
            )
            repository.updateProfile(updated)
            _saveStatus.value = "Personal details updated"
        }
    }

    fun updateEducation(educationList: List<Education>) {
        viewModelScope.launch {
            val current = userProfile.value ?: JobSeeker()
            val updated = current.copy(educationList = educationList)
            repository.updateProfile(updated)
            educationList.forEach { edu ->
                repository.saveEducation(edu)
            }
            _saveStatus.value = "Education details saved"
        }
    }

    fun updateSkillsAndPreferences(skills: List<String>, roles: List<String>, locations: List<String>) {
        viewModelScope.launch {
            val current = userProfile.value ?: JobSeeker()
            val updated = current.copy(
                skillsList = skills,
                preferredRoles = roles,
                preferredLocations = locations
            )
            repository.updateProfile(updated)
            repository.updateSkills(skills)
            repository.updatePreferences(roles, locations)
            _saveStatus.value = "Skills & Preferences saved"
        }
    }
}
