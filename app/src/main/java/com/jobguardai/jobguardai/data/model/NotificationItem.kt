package com.jobguardai.jobguardai.data.model

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: String // ApplicationStatus, JobAlert, SkillGap, System
)
