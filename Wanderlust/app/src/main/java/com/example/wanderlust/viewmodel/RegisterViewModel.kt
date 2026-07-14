package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AuthRepository
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "USER",
    val businessSubtype: String = "TOURS",
    val companyName: String = "",
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
        uiState = uiState.copy(
            name = value.take(Validation.NAME_MAX),
            errorMessage = null,
            isEmailDuplicate = false,
        )
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value.take(Validation.EMAIL_MAX),
            errorMessage = null,
            isEmailDuplicate = false,
        )
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(
            password = value.take(Validation.PASSWORD_MAX),
            errorMessage = null,
            isEmailDuplicate = false,
        )
    }

    fun onRoleChange(value: String) {
        uiState = uiState.copy(
            role = value,
            errorMessage = null,
            companyName = if (value == "BUSINESS") uiState.companyName else "",
            businessSubtype = if (value == "BUSINESS") uiState.businessSubtype else "TOURS",
        )
    }

    fun onBusinessSubtypeChange(value: String) {
        uiState = uiState.copy(businessSubtype = value, errorMessage = null)
    }

    fun onCompanyNameChange(value: String) {
        uiState = uiState.copy(
            companyName = value.take(Validation.COMPANY_MAX),
            errorMessage = null,
        )
    }

    fun register() {
        val name = uiState.name.trim()
        val email = Validation.normalizeEmail(uiState.email)
        val password = uiState.password
        val role = if (uiState.role == "BUSINESS") "BUSINESS" else "USER"
        val companyName = uiState.companyName.trim()
        val subtype =
            if (role == "BUSINESS" && uiState.businessSubtype == "TRANSPORT") "TRANSPORT" else "TOURS"
        Validation.validateRegister(
            name = name,
            email = email,
            password = password,
            isBusiness = role == "BUSINESS",
            companyName = companyName,
        )?.let {
            uiState = uiState.copy(errorMessage = it)
            return
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
                role = role,
                companyName = companyName,
                businessSubtype = subtype,
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

    private fun isDuplicateEmailError(message: String?): Boolean {
        val msg = message.orEmpty().lowercase()
        return msg.contains("already registered") ||
            msg.contains("already used") ||
            msg.contains("email already") ||
            msg.contains("duplicate")
    }
}
