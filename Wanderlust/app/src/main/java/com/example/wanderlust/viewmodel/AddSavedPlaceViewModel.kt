package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.model.CustomPlaceInput
import com.example.wanderlust.data.repository.FavoriteRepository
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

data class AddSavedPlaceUiState(
    val title: String = "",
    val location: String = "",
    val notes: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val mapsLink: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val savedDestination: DestinationCard? = null,
)

class AddSavedPlaceViewModel(
    private val repository: FavoriteRepository = FavoriteRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(AddSavedPlaceUiState())
        private set

    fun onTitleChange(v: String) {
        uiState = uiState.copy(title = v.take(Validation.TITLE_MAX), errorMessage = null)
    }

    fun onLocationChange(v: String) {
        uiState = uiState.copy(location = v.take(Validation.LOCATION_MAX), errorMessage = null)
    }

    fun onNotesChange(v: String) {
        uiState = uiState.copy(notes = v.take(Validation.DESCRIPTION_MAX))
    }

    fun onLatitudeChange(v: String) {
        uiState = uiState.copy(latitude = v.filter { it.isDigit() || it == '.' || it == '-' })
    }

    fun onLongitudeChange(v: String) {
        uiState = uiState.copy(longitude = v.filter { it.isDigit() || it == '.' || it == '-' })
    }

    fun onMapsLinkChange(v: String) {
        uiState = uiState.copy(mapsLink = v.take(500))
    }

    fun save() {
        val titleErr = Validation.requirePlaceTitle(uiState.title)
        val locErr = Validation.requireLocation(uiState.location)
        if (titleErr != null || locErr != null) {
            uiState = uiState.copy(errorMessage = titleErr ?: locErr)
            return
        }
        val latRaw = uiState.latitude.trim()
        val lngRaw = uiState.longitude.trim()
        val lat = latRaw.toDoubleOrNull()
        val lng = lngRaw.toDoubleOrNull()
        if (latRaw.isNotEmpty() || lngRaw.isNotEmpty()) {
            if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
                uiState = uiState.copy(
                    errorMessage = if (com.example.wanderlust.locale.AppLocale.isKhmer) {
                        "រយៈទទឹង/រយៈបណ្តោយមិនត្រឹមត្រូវ"
                    } else {
                        "Latitude/longitude are invalid"
                    },
                )
                return
            }
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            repository.addCustomPlace(
                CustomPlaceInput(
                    title = uiState.title.trim(),
                    location = uiState.location.trim(),
                    notes = uiState.notes.trim(),
                    latitude = lat,
                    longitude = lng,
                    mapsLink = uiState.mapsLink.trim(),
                ),
            )
                .onSuccess { card ->
                    uiState = uiState.copy(
                        isLoading = false,
                        savedDestination = card,
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not save place",
                    )
                }
        }
    }

    fun clearSaved() {
        uiState = uiState.copy(savedDestination = null)
    }
}
