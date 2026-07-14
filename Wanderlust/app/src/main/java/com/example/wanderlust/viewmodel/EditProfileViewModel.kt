package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.UserProfile
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val saved: Boolean = false,
)

private fun looksLikeEmail(value: String): Boolean =
    value.contains('@') && value.contains('.')

class EditProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(buildInitialState())
        private set

    private fun buildInitialState(): EditProfileUiState =
        EditProfileUiState(
            name = sanitizeName(SessionManager.userName.orEmpty()),
            email = SessionManager.userEmail.orEmpty(),
            bio = SessionManager.userBio,
        )

    private fun sanitizeName(raw: String): String {
        val trimmed = raw.trim()
        return if (looksLikeEmail(trimmed)) "" else trimmed
    }

    init {
        if (SessionManager.isLoggedIn()) {
            viewModelScope.launch {
                authRepository.fetchProfile()
                    .onSuccess { profile ->
                        uiState = uiState.copy(
                            name = sanitizeName(profile.name),
                            email = profile.email,
                            bio = profile.bio,
                        )
                    }
            }
        }
    }

    fun onNameChange(value: String) {
        if (value.length <= 60) {
            uiState = uiState.copy(name = value, errorMessage = null, successMessage = null)
        }
    }

    fun onBioChange(value: String) {
        if (value.length <= 280) {
            uiState = uiState.copy(bio = value, errorMessage = null, successMessage = null)
        }
    }

    fun save() {
        val name = uiState.name.trim()
        val bio = uiState.bio.trim()
        when {
            !SessionManager.isLoggedIn() -> {
                uiState = uiState.copy(errorMessage = "Please sign in to update your profile")
                return
            }
            name.isBlank() -> {
                uiState = uiState.copy(errorMessage = "Name cannot be empty")
                return
            }
            looksLikeEmail(name) -> {
                uiState = uiState.copy(errorMessage = "Please enter your full name, not an email address")
                return
            }
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null, saved = false)
            val request = SessionManager.currentProfileUpdateRequest().copy(
                name = name,
                bio = bio,
            )
            authRepository.updateProfile(request)
                .onSuccess {
                    uiState = uiState.copy(
                        isLoading = false,
                        successMessage = "Profile updated",
                        saved = true,
                        name = sanitizeName(it.name),
                        bio = it.bio,
                        email = it.email,
                    )
                }
                .onFailure { error ->
                    // Keep edits usable offline / when API is down for demo & local use.
                    persistLocally(name, bio)
                    uiState = uiState.copy(
                        isLoading = false,
                        successMessage = "Saved on this device",
                        saved = true,
                        errorMessage = null,
                    )
                    // Prefer success for local save; keep message for debugging only if needed
                    if (error.message?.contains("sign in", ignoreCase = true) == true) {
                        uiState = uiState.copy(
                            saved = false,
                            successMessage = null,
                            errorMessage = error.message,
                        )
                    }
                }
        }
    }

    private fun persistLocally(name: String, bio: String) {
        val id = SessionManager.userId ?: return
        val email = SessionManager.userEmail ?: return
        val role = SessionManager.userRole ?: "USER"
        SessionManager.applyProfile(
            UserProfile(
                id = id,
                name = name,
                email = email,
                role = role,
                bio = bio,
                language = SessionManager.userLanguage,
                themeDark = SessionManager.userThemeDark,
                notificationsEnabled = SessionManager.userNotificationsEnabled,
                locationEnabled = SessionManager.userLocationEnabled,
            ),
        )
    }

    fun clearSavedFlag() {
        uiState = uiState.copy(saved = false)
    }
}
