package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.ProfileUpdateRequest
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class SettingsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(
        SettingsUiState(
            notificationsEnabled = SessionManager.userNotificationsEnabled,
            locationEnabled = SessionManager.userLocationEnabled,
        ),
    )
        private set

    init {
        if (SessionManager.isLoggedIn()) {
            viewModelScope.launch {
                authRepository.fetchProfile()
                    .onSuccess {
                        uiState = uiState.copy(
                            notificationsEnabled = it.notificationsEnabled,
                            locationEnabled = it.locationEnabled,
                        )
                    }
            }
        }
    }

    fun onNotificationsChange(enabled: Boolean) {
        uiState = uiState.copy(notificationsEnabled = enabled, errorMessage = null)
        persistPreferences(
            SessionManager.currentProfileUpdateRequest().copy(notificationsEnabled = enabled),
        )
    }

    fun onLocationChange(enabled: Boolean) {
        uiState = uiState.copy(locationEnabled = enabled, errorMessage = null)
        persistPreferences(
            SessionManager.currentProfileUpdateRequest().copy(locationEnabled = enabled),
        )
    }

    fun saveLanguage(raw: String) {
        SessionManager.setLanguage(raw)
        persistPreferences(
            SessionManager.currentProfileUpdateRequest().copy(
                language = SessionManager.userLanguage,
            ),
        )
    }

    fun saveTheme(themeDark: Boolean) {
        SessionManager.setThemeDark(themeDark)
        persistPreferences(
            SessionManager.currentProfileUpdateRequest().copy(themeDark = themeDark),
        )
    }

    private fun persistPreferences(request: ProfileUpdateRequest) {
        if (!SessionManager.isLoggedIn()) return
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null)
            authRepository.updateProfile(request)
                .onSuccess {
                    uiState = uiState.copy(
                        isSaving = false,
                        notificationsEnabled = it.notificationsEnabled,
                        locationEnabled = it.locationEnabled,
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSaving = false,
                        errorMessage = error.message,
                        notificationsEnabled = SessionManager.userNotificationsEnabled,
                        locationEnabled = SessionManager.userLocationEnabled,
                    )
                }
        }
    }
}
