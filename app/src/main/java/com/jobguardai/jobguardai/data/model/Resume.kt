package com.jobguardai.jobguardai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey val id: String,
    @SerializedName("file_name", alternate = ["fileName"]) val fileName: String,
    @SerializedName("file_uri", alternate = ["fileUri"]) val fileUri: String,
    @SerializedName("upload_date", alternate = ["uploadDate"]) val uploadDate: String,
    @SerializedName("file_size", alternate = ["fileSize"]) val fileSize: String = "1.2 MB",
    @SerializedName("parsed_text", alternate = ["parsedText"]) val parsedText: String = "",
    @SerializedName("extracted_skills", alternate = ["extractedSkills"]) val extractedSkills: List<String> = emptyList(),
    @SerializedName("extracted_education", alternate = ["extractedEducation"]) val extractedEducation: List<String> = emptyList(),
    @SerializedName("extracted_experience", alternate = ["extractedExperience"]) val extractedExperience: List<String> = emptyList(),
    @SerializedName("missing_sections", alternate = ["missingSections"]) val missingSections: List<String> = emptyList(),
    @SerializedName("improvement_suggestions", alternate = ["improvementSuggestions"]) val improvementSuggestions: List<String> = emptyList(),
    @SerializedName("overall_score", alternate = ["overallScore"]) val overallScore: Int = 85,
    @SerializedName("is_default", alternate = ["isDefault"]) val isDefault: Boolean = true
)
