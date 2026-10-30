package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.NotificationItem
import com.jobguardai.jobguardai.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    val notifications: StateFlow<List<NotificationItem>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }
}

