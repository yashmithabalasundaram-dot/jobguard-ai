package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey val id: String,
    val title: String,
    val company: String = "",
    @SerializedName("company_name", alternate = ["companyTitle"]) val companyNameAlt: String? = null,
    @SerializedName("company_logo", alternate = ["companyLogo"]) val companyLogo: String = "",
    val location: String = "Remote",
    val type: String = "Full-Time", // Full-Time, Remote, Contract, Internship
    val salary: String = "Competitive",
    val experience: String = "Not Specified",
    val description: String = "",
    val requirements: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    @SerializedName("is_saved", alternate = ["isSaved"]) val isSaved: Boolean = false,
    @SerializedName("is_scam_flagged", alternate = ["isScamFlagged"]) val isScamFlagged: Boolean = false,
    @SerializedName("scam_reason", alternate = ["scamReason"]) val scamReason: String? = null,
    val deadline: String = "",
    @SerializedName("match_score", alternate = ["matchScore"]) val matchScore: Int = 85,
    val isDemo: Boolean = false,
    @SerializedName("external_url", alternate = ["externalUrl"]) val externalUrl: String = ""
) {
    // Helper to get effective company name
    val effectiveCompany: String
        get() = company.ifBlank { companyNameAlt ?: "Verified Partner" }
}
