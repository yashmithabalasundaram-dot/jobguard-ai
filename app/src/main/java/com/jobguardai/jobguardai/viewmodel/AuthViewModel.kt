package com.jobguardai.jobguardai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobguardai.jobguardai.data.model.JobSeeker
import com.jobguardai.jobguardai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: JobSeeker) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser = repository.currentUser
    val isLoggedIn = repository.isLoggedIn
    val lastGeneratedOtp = repository.lastGeneratedOtp

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess {
                _uiState.value = AuthUiState.Success(it)
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.register(name, email, phone, pass)
            result.onSuccess {
                _uiState.value = AuthUiState.Success(it)
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Registration failed")
            }
        }
    }

    suspend fun verifyEmail(otp: String): Result<Boolean> {
        return repository.verifyEmail(otp, currentUser.value?.email)
    }

    suspend fun resendOtp(): Result<String> {
        return repository.resendOtp(currentUser.value?.email)
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return repository.forgotPassword(email)
    }

    suspend fun resetPassword(token: String, newPass: String): Result<Boolean> {
        return repository.resetPassword(token, newPass)
    }

    suspend fun changePassword(currentPass: String, newPass: String): Result<Boolean> {
        return repository.changePassword(currentPass, newPass)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }
}
