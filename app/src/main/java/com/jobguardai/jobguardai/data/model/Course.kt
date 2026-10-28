package com.jobguardai.jobguardai.data.model

data class Course(
    val id: String,
    val title: String,
    val provider: String,
    val duration: String,
    val level: String, // Beginner, Intermediate, Advanced
    val skillCategory: String,
    val progressPercent: Int = 0,
    val url: String = "",
    val description: String = "",
    val modules: List<String> = emptyList(),
    val isCompleted: Boolean = false
)
