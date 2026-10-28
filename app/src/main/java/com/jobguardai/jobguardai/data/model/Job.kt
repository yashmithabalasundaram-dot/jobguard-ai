package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val companyLogo: String = "",
    val location: String,
    val type: String, // Full-Time, Remote, Contract, Internship
    val salary: String,
    val experience: String,
    val description: String,
    val requirements: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val isScamFlagged: Boolean = false,
    val scamReason: String? = null,
    val deadline: String = "",
    val matchScore: Int = 85,
    val isDemo: Boolean = true
)
