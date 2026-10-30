package com.jobguardai.jobguardai.data.model

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    @SerializedName("is_read", alternate = ["isRead"]) val isRead: Boolean = false,
    val type: String = "System" // ApplicationStatus, JobAlert, SkillGap, System
)
