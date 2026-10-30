package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "job_applications")
data class JobApplication(
    @PrimaryKey val id: String,
    @SerializedName("job_id", alternate = ["jobId"]) val jobId: String,
    @SerializedName("job_title", alternate = ["jobTitle"]) val jobTitle: String = "",
    @SerializedName("company_name", alternate = ["companyName"]) val companyName: String = "",
    @SerializedName("company_logo", alternate = ["companyLogo"]) val companyLogo: String = "",
    @SerializedName("applied_date", alternate = ["appliedDate"]) val appliedDate: String = "",
    val status: String = "Submitted", // Submitted, Under Review, Interview Scheduled, Accepted, Rejected
    @SerializedName("resume_id", alternate = ["resumeId"]) val resumeId: String = "",
    @SerializedName("cover_note", alternate = ["coverNote"]) val coverNote: String = "",
    val isDemo: Boolean = false
)
