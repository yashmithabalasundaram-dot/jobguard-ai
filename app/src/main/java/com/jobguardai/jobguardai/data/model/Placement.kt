package com.jobguardai.jobguardai.data.model

data class Placement(
    val id: String,
    val companyName: String,
    val companyLogo: String = "",
    val driveTitle: String,
    val role: String,
    val packageAmount: String,
    val eligibilityCriteria: String,
    val driveDate: String,
    val location: String,
    val status: String, // Upcoming, Ongoing, Closed
    val description: String,
    val rounds: List<String> = emptyList(),
    val registrationDeadline: String = ""
)
