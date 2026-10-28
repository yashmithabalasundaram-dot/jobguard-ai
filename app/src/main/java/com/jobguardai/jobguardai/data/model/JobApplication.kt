package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_applications")
data class JobApplication(
    @PrimaryKey val id: String,
    val jobId: String,
    val jobTitle: String,
    val companyName: String,
    val companyLogo: String = "",
    val appliedDate: String,
    val status: String, // Submitted, Under Review, Interview Scheduled, Accepted, Rejected
    val resumeId: String,
    val coverNote: String = "",
    val isDemo: Boolean = true
)
