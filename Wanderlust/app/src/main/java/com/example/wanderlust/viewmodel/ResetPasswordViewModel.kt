package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false,
)

class ResetPasswordViewModel(
    initialEmail: String = "",
    initialToken: String = "",
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(
        ResetPasswordUiState(email = initialEmail, token = initialToken),
    )
        private set

    fun onEmailChange(value: String) = update { copy(email = value, errorMessage = null) }
    fun onTokenChange(value: String) = update { copy(token = value, errorMessage = null) }
    fun onNewPasswordChange(value: String) = update { copy(newPassword = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = update { copy(confirmPassword = value, errorMessage = null) }

    fun resetPassword() {
        val s = uiState
        when {
            s.email.isBlank() || s.token.isBlank() || s.newPassword.isBlank() ->
                update { copy(errorMessage = "Please fill all fields") }
            s.newPassword.length < 6 ->
                update { copy(errorMessage = "Password must be at least 6 characters") }
            s.newPassword != s.confirmPassword ->
                update { copy(errorMessage = "Passwords do not match") }
            else -> viewModelScope.launch {
                update { copy(isLoading = true, errorMessage = null, success = false) }
                repository.resetPassword(s.email, s.token, s.newPassword)
                    .onSuccess { update { copy(isLoading = false, success = true) } }
                    .onFailure { e -> update { copy(isLoading = false, errorMessage = e.message ?: "Reset failed") } }
            }
        }
    }

    fun clearSuccess() = update { copy(success = false) }

    private fun update(block: ResetPasswordUiState.() -> ResetPasswordUiState) {
        uiState = uiState.block()
    }
}
