package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

class ChangePasswordViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(ChangePasswordUiState())
        private set

    fun onCurrentChange(value: String) = update { copy(currentPassword = value, errorMessage = null) }
    fun onNewChange(value: String) = update { copy(newPassword = value, errorMessage = null) }
    fun onConfirmChange(value: String) = update { copy(confirmPassword = value, errorMessage = null) }

    fun changePassword() {
        val s = uiState
        when {
            s.currentPassword.isBlank() || s.newPassword.isBlank() ->
                update { copy(errorMessage = "Please fill all fields") }
            s.newPassword.length < 6 ->
                update { copy(errorMessage = "New password must be at least 6 characters") }
            s.newPassword != s.confirmPassword ->
                update { copy(errorMessage = "Passwords do not match") }
            else -> viewModelScope.launch {
                update { copy(isLoading = true, errorMessage = null, successMessage = null) }
                repository.changePassword(s.currentPassword, s.newPassword)
                    .onSuccess { response ->
                        update {
                            copy(
                                isLoading = false,
                                successMessage = response.message,
                                currentPassword = "",
                                newPassword = "",
                                confirmPassword = "",
                            )
                        }
                    }
                    .onFailure { e ->
                        update { copy(isLoading = false, errorMessage = e.message ?: "Change failed") }
                    }
            }
        }
    }

    private fun update(block: ChangePasswordUiState.() -> ChangePasswordUiState) {
        uiState = uiState.block()
    }
}
