package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import com.example.wanderlust.util.Validation
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

    fun onEmailChange(value: String) =
        update { copy(email = value.take(Validation.EMAIL_MAX), errorMessage = null) }

    fun onTokenChange(value: String) =
        update { copy(token = value.filter { it.isLetterOrDigit() }.take(8), errorMessage = null) }

    fun onNewPasswordChange(value: String) =
        update { copy(newPassword = value.take(Validation.PASSWORD_MAX), errorMessage = null) }

    fun onConfirmPasswordChange(value: String) =
        update { copy(confirmPassword = value.take(Validation.PASSWORD_MAX), errorMessage = null) }

    fun resetPassword() {
        val s = uiState
        val error = Validation.requireEmail(s.email)
            ?: Validation.requireResetToken(s.token)
            ?: Validation.requirePassword(s.newPassword, "New password", "ពាក្យសម្ងាត់ថ្មី")
            ?: Validation.passwordsMatch(s.newPassword, s.confirmPassword)
        if (error != null) {
            update { copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null, success = false) }
            repository.resetPassword(
                Validation.normalizeEmail(s.email),
                s.token.trim().uppercase(),
                s.newPassword,
            )
                .onSuccess { update { copy(isLoading = false, success = true) } }
                .onFailure { e ->
                    update { copy(isLoading = false, errorMessage = e.message ?: "Reset failed") }
                }
        }
    }

    fun clearSuccess() = update { copy(success = false) }

    private fun update(block: ResetPasswordUiState.() -> ResetPasswordUiState) {
        uiState = uiState.block()
    }
}
