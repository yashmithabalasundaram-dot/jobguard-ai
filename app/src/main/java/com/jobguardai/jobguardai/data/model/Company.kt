package com.jobguardai.jobguardai.data.model

data class Company(
    val id: String,
    val name: String,
    val logoUrl: String = "",
    val location: String,
    val industry: String,
    val description: String,
    val website: String,
    val rating: Double = 4.5,
    val employeeCount: String = "100-500",
    val openPositionsCount: Int = 12,
    val isVerified: Boolean = true
)
