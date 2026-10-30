package com.jobguardai.jobguardai.data.model

import com.google.gson.annotations.SerializedName

data class Placement(
    val id: String,
    @SerializedName("company_name", alternate = ["companyName"]) val companyName: String,
    @SerializedName("company_logo", alternate = ["companyLogo"]) val companyLogo: String = "",
    @SerializedName("drive_title", alternate = ["driveTitle"]) val driveTitle: String,
    val role: String,
    @SerializedName("package_amount", alternate = ["packageAmount"]) val packageAmount: String,
    @SerializedName("eligibility_criteria", alternate = ["eligibilityCriteria"]) val eligibilityCriteria: String,
    @SerializedName("drive_date", alternate = ["driveDate"]) val driveDate: String,
    val location: String,
    val status: String = "Upcoming", // Upcoming, Ongoing, Closed
    val description: String = "",
    val rounds: List<String> = emptyList(),
    @SerializedName("registration_deadline", alternate = ["registrationDeadline"]) val registrationDeadline: String = ""
)
