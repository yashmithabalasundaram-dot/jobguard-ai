package com.jobguardai.jobguardai.data.model

import com.google.gson.annotations.SerializedName

data class Education(
    @SerializedName("id") val id: String? = null,
    @SerializedName("degree") val degree: String,
    @SerializedName("institution") val institution: String,
    @SerializedName("field_of_study", alternate = ["fieldOfStudy"]) val fieldOfStudy: String = "",
    @SerializedName("start_year", alternate = ["startYear"]) val startYear: String = "",
    @SerializedName("end_year", alternate = ["endYear"]) val endYear: String = "",
    @SerializedName("grade") val grade: String = ""
)

data class Experience(
    @SerializedName("title") val title: String,
    @SerializedName("company") val company: String,
    @SerializedName("location") val location: String = "",
    @SerializedName("start_date", alternate = ["startDate"]) val startDate: String = "",
    @SerializedName("end_date", alternate = ["endDate"]) val endDate: String = "",
    @SerializedName("description") val description: String = ""
)

data class JobSeeker(
    @SerializedName("id") val id: String = "user_1",
    @SerializedName("full_name", alternate = ["fullName"]) val fullName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("bio") val bio: String = "",
    @SerializedName("headline") val headline: String = "",
    @SerializedName("location") val location: String = "",
    @SerializedName("educationList") val educationList: List<Education> = emptyList(),
    @SerializedName("experienceList") val experienceList: List<Experience> = emptyList(),
    @SerializedName("skillsList") val skillsList: List<String> = emptyList(),
    @SerializedName("preferredRoles") val preferredRoles: List<String> = emptyList(),
    @SerializedName("preferredLocations") val preferredLocations: List<String> = emptyList(),
    @SerializedName("resumeUrl") val resumeUrl: String? = null,
    @SerializedName("is_verified", alternate = ["isVerified"]) val isVerified: Boolean = false,
    @SerializedName("consentSharedWithCompanies") val consentSharedWithCompanies: Boolean = false,
    @SerializedName("profile_completion_pct", alternate = ["profileCompletionPct"]) val profileCompletionPct: Int = 20
)
