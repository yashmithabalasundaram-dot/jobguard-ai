package com.jobguardai.jobguardai.data.model

data class Education(
    val degree: String,
    val institution: String,
    val fieldOfStudy: String,
    val startYear: String,
    val endYear: String,
    val grade: String
)

data class Experience(
    val title: String,
    val company: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val description: String
)

data class JobSeeker(
    val id: String = "user_1",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val headline: String = "",
    val location: String = "",
    val educationList: List<Education> = emptyList(),
    val experienceList: List<Experience> = emptyList(),
    val skillsList: List<String> = emptyList(),
    val preferredRoles: List<String> = emptyList(),
    val preferredLocations: List<String> = emptyList(),
    val resumeUrl: String? = null,
    val consentSharedWithCompanies: Boolean = false
)
