package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.UserProfile
import com.example.wanderlust.data.repository.AuthRepository
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val phone: String = "",
    val city: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val nationality: String = "Cambodia",
    val travelStyle: String = "",
    val emergencyContact: String = "",
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
            phone = SessionManager.userPhone,
            city = SessionManager.userCity,
            gender = SessionManager.userGender,
            birthDate = SessionManager.userBirthDate,
            nationality = SessionManager.userNationality.ifBlank { "Cambodia" },
            travelStyle = SessionManager.userTravelStyle,
            emergencyContact = SessionManager.userEmergencyContact,
        )

    private fun sanitizeName(raw: String): String {
        val trimmed = raw.trim()
        return if (looksLikeEmail(trimmed)) "" else trimmed
    }

    init {
        if (SessionManager.isLoggedIn()) {
            viewModelScope.launch {
                authRepository.fetchProfile()
                    .onSuccess { profile -> applyProfileToState(profile) }
            }
        }
    }

    private fun applyProfileToState(profile: UserProfile) {
        uiState = uiState.copy(
            name = sanitizeName(profile.name),
            email = profile.email,
            bio = profile.bio,
            phone = profile.phone,
            city = profile.city,
            gender = profile.gender,
            birthDate = profile.birthDate,
            nationality = profile.nationality.ifBlank { "Cambodia" },
            travelStyle = profile.travelStyle,
            emergencyContact = profile.emergencyContact,
        )
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

    fun onPhoneChange(value: String) {
        if (value.length <= 40) {
            uiState = uiState.copy(phone = value, errorMessage = null, successMessage = null)
        }
    }

    fun onCityChange(value: String) {
        if (value.length <= 100) {
            uiState = uiState.copy(city = value, errorMessage = null, successMessage = null)
        }
    }

    fun onGenderChange(value: String) {
        uiState = uiState.copy(gender = value, errorMessage = null, successMessage = null)
    }

    fun onBirthDateChange(value: String) {
        if (value.length <= 20) {
            uiState = uiState.copy(birthDate = value, errorMessage = null, successMessage = null)
        }
    }

    fun onNationalityChange(value: String) {
        if (value.length <= 80) {
            uiState = uiState.copy(nationality = value, errorMessage = null, successMessage = null)
        }
    }

    fun onTravelStyleChange(value: String) {
        uiState = uiState.copy(travelStyle = value, errorMessage = null, successMessage = null)
    }

    fun onEmergencyContactChange(value: String) {
        if (value.length <= 120) {
            uiState = uiState.copy(emergencyContact = value, errorMessage = null, successMessage = null)
        }
    }

    fun save() {
        val name = uiState.name.trim()
        when {
            !SessionManager.isLoggedIn() -> {
                uiState = uiState.copy(errorMessage = "Please sign in to update your profile")
                return
            }
        }
        Validation.requireName(name)?.let {
            uiState = uiState.copy(errorMessage = it)
            return
        }
        Validation.optionalPhone(uiState.phone)?.let {
            uiState = uiState.copy(errorMessage = it)
            return
        }
        Validation.optionalBirthDate(uiState.birthDate)?.let {
            uiState = uiState.copy(errorMessage = it)
            return
        }
        if (uiState.city.trim().length > Validation.CITY_MAX) {
            uiState = uiState.copy(
                errorMessage = if (AppLocale.isKhmer) "ទីក្រុងវែងពេក" else "City is too long",
            )
            return
        }
        if (uiState.bio.trim().length > Validation.BIO_MAX) {
            uiState = uiState.copy(
                errorMessage = if (AppLocale.isKhmer) "Bio វែងពេក" else "Bio is too long",
            )
            return
        }
        if (uiState.emergencyContact.trim().length > 120) {
            uiState = uiState.copy(
                errorMessage = if (AppLocale.isKhmer) "ទំនាក់ទំនងបន្ទាន់វែងពេក" else "Emergency contact is too long",
            )
            return
        }
        val snapshot = uiState
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, successMessage = null, saved = false)
            val request = SessionManager.currentProfileUpdateRequest().copy(
                name = name,
                bio = snapshot.bio.trim(),
                phone = snapshot.phone.trim(),
                city = snapshot.city.trim(),
                gender = snapshot.gender.trim(),
                birthDate = snapshot.birthDate.trim(),
                nationality = snapshot.nationality.trim().ifBlank { "Cambodia" },
                travelStyle = snapshot.travelStyle.trim(),
                emergencyContact = snapshot.emergencyContact.trim(),
            )
            authRepository.updateProfile(request)
                .onSuccess {
                    applyProfileToState(it)
                    uiState = uiState.copy(
                        isLoading = false,
                        successMessage = "Profile updated",
                        saved = true,
                    )
                }
                .onFailure { error ->
                    persistLocally(request)
                    if (error.message?.contains("sign in", ignoreCase = true) == true) {
                        uiState = uiState.copy(
                            isLoading = false,
                            saved = false,
                            successMessage = null,
                            errorMessage = error.message,
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            successMessage = "Saved on this device",
                            saved = true,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private fun persistLocally(request: com.example.wanderlust.data.model.ProfileUpdateRequest) {
        val id = SessionManager.userId ?: return
        val email = SessionManager.userEmail ?: return
        val role = SessionManager.userRole ?: "USER"
        SessionManager.applyProfile(
            UserProfile(
                id = id,
                name = request.name,
                email = email,
                role = role,
                bio = request.bio,
                phone = request.phone,
                city = request.city,
                gender = request.gender,
                birthDate = request.birthDate,
                nationality = request.nationality,
                travelStyle = request.travelStyle,
                emergencyContact = request.emergencyContact,
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
