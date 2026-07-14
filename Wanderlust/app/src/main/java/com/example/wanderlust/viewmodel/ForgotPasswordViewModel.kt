package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resetToken: String? = null,
    val successMessage: String? = null,
)

class ForgotPasswordViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(ForgotPasswordUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value.take(Validation.EMAIL_MAX), errorMessage = null)
    }

    fun requestCode() {
        val email = Validation.normalizeEmail(uiState.email)
        Validation.requireEmail(email)?.let {
            uiState = uiState.copy(errorMessage = it)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, resetToken = null, successMessage = null)
            repository.forgotPassword(email)
                .onSuccess { response ->
                    uiState = uiState.copy(
                        isLoading = false,
                        successMessage = response.message,
                        resetToken = response.resetToken,
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Request failed",
                    )
                }
        }
    }
}
