package com.jobguardai.jobguardai.data.repository

import com.jobguardai.jobguardai.data.model.NotificationItem
import com.jobguardai.jobguardai.data.remote.ApiService
import com.jobguardai.jobguardai.data.remote.IdRequest
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class NotificationRepository(
    private val apiService: ApiService = RetrofitClient.getApiService()
) {
    private val demoNotifications = listOf(
        NotificationItem("notif_1", "Application Status Updated", "TechCorp Solutions reviewed your application.", "10 mins ago", false, "ApplicationStatus"),
        NotificationItem("notif_2", "New Matching Job Found", "Senior Android Developer role at InnovateX matches 88% of your profile.", "2 hours ago", false, "JobAlert")
    )

    private val _notifications = MutableStateFlow<List<NotificationItem>>(demoNotifications)
    val notifications = _notifications.asStateFlow()

    fun getNotifications(): Flow<List<NotificationItem>> = flow {
        try {
            val response = apiService.getNotifications()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()!!.data ?: emptyList()
                if (list.isNotEmpty()) {
                    _notifications.value = list
                    emit(list)
                    return@flow
                }
            }
        } catch (e: Exception) {
            // Fallback
        }
        emit(_notifications.value)
    }

    suspend fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        try {
            apiService.markNotificationRead(IdRequest(id))
        } catch (e: Exception) {
            // Ignore
        }
    }
}

