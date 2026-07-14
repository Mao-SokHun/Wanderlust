package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailDuplicate: Boolean = false,
    val registerSuccess: Boolean = false,
)

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, errorMessage = null, isEmailDuplicate = false)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, errorMessage = null, isEmailDuplicate = false)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null, isEmailDuplicate = false)
    }

    fun register() {
        val name = uiState.name.trim()
        val email = uiState.email.trim().lowercase()
        val password = uiState.password
        when {
            name.isBlank() || email.isBlank() || password.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Please fill all fields")
                return
            }
            name.length < 2 -> {
                uiState = uiState.copy(errorMessage = "Name is too short")
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
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null,
                isEmailDuplicate = false,
                registerSuccess = false,
            )
            repository.register(
                name = name,
                email = email,
                password = password,
            ).onSuccess {
                uiState = uiState.copy(isLoading = false, registerSuccess = true)
            }.onFailure { error ->
                val duplicate = isDuplicateEmailError(error.message)
                uiState = uiState.copy(
                    isLoading = false,
                    isEmailDuplicate = duplicate,
                    errorMessage = if (duplicate) null else (error.message ?: "Register failed"),
                )
            }
        }
    }

    fun resetSuccess() {
        uiState = uiState.copy(registerSuccess = false)
    }

    private fun isValidEmail(value: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()

    private fun isDuplicateEmailError(message: String?): Boolean {
        val msg = message.orEmpty().lowercase()
        return msg.contains("already registered") ||
            msg.contains("already used") ||
            msg.contains("email already") ||
            msg.contains("duplicate")
    }
}
