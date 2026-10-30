package com.jobguardai.jobguardai.data.model

import com.google.gson.annotations.SerializedName

data class Company(
    val id: String,
    val name: String,
    @SerializedName("logo_url", alternate = ["logoUrl"]) val logoUrl: String = "",
    val location: String = "",
    val industry: String = "",
    val description: String = "",
    val website: String = "",
    val rating: Double = 4.5,
    @SerializedName("employee_count", alternate = ["employeeCount"]) val employeeCount: String = "100-500",
    @SerializedName("open_positions_count", alternate = ["openPositionsCount"]) val openPositionsCount: Int = 12,
    @SerializedName("is_verified", alternate = ["isVerified"]) val isVerified: Boolean = true
)
