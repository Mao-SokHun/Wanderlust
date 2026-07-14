package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import com.example.wanderlust.util.Validation
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

    fun onCurrentChange(value: String) =
        update { copy(currentPassword = value.take(Validation.PASSWORD_MAX), errorMessage = null) }

    fun onNewChange(value: String) =
        update { copy(newPassword = value.take(Validation.PASSWORD_MAX), errorMessage = null) }

    fun onConfirmChange(value: String) =
        update { copy(confirmPassword = value.take(Validation.PASSWORD_MAX), errorMessage = null) }

    fun changePassword() {
        val s = uiState
        val error = Validation.requirePassword(s.currentPassword, "Current password", "ពាក្យសម្ងាត់បច្ចុប្បន្ន")
            ?: Validation.requirePassword(s.newPassword, "New password", "ពាក្យសម្ងាត់ថ្មី")
            ?: Validation.requireDifferentPasswords(s.currentPassword, s.newPassword)
            ?: Validation.passwordsMatch(s.newPassword, s.confirmPassword)
        if (error != null) {
            update { copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
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

    private fun update(block: ChangePasswordUiState.() -> ChangePasswordUiState) {
        uiState = uiState.block()
    }
}
