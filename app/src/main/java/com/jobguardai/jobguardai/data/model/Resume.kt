package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey val id: String,
    val fileName: String,
    val fileUri: String,
    val uploadDate: String,
    val fileSize: String = "1.2 MB",
    val parsedText: String = "",
    val extractedSkills: List<String> = emptyList(),
    val extractedEducation: List<String> = emptyList(),
    val extractedExperience: List<String> = emptyList(),
    val missingSections: List<String> = emptyList(),
    val improvementSuggestions: List<String> = emptyList(),
    val overallScore: Int = 85,
    val isDefault: Boolean = true
)
