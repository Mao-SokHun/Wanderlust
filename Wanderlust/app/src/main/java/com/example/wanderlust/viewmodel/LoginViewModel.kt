package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "user@test.com",
    val password: String = if (BuildConfig.DEBUG) "123456" else "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    fun login() {
        val email = uiState.email.trim()
        val password = uiState.password
        when {
            email.isBlank() || password.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Please enter email and password")
                return
            }
            !isValidEmail(email) -> {
                uiState = uiState.copy(errorMessage = "Please enter a valid email")
                return
            }
            password.length < 6 -> {
                uiState = uiState.copy(errorMessage = "Password must be at least 6 characters")
                return
            }
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, loginSuccess = false)
            repository.login(email, password)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, loginSuccess = true)
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed",
                    )
                }
        }
    }

    fun resetSuccess() {
        uiState = uiState.copy(loginSuccess = false)
    }

    fun fillTestUser() {
        uiState = uiState.copy(
            email = "user@test.com",
            password = "123456",
            errorMessage = null,
        )
    }

    fun fillTestAdmin() {
        uiState = uiState.copy(
            email = "admin@test.com",
            password = "admin123",
            errorMessage = null,
        )
    }

    private fun isValidEmail(value: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()
}
