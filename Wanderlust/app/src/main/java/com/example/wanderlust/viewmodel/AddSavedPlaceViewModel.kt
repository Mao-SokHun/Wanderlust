package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.model.CustomPlaceInput
import com.example.wanderlust.data.repository.FavoriteRepository
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

    fun onTitleChange(v: String) { uiState = uiState.copy(title = v, errorMessage = null) }
    fun onLocationChange(v: String) { uiState = uiState.copy(location = v, errorMessage = null) }
    fun onNotesChange(v: String) { uiState = uiState.copy(notes = v) }
    fun onLatitudeChange(v: String) { uiState = uiState.copy(latitude = v) }
    fun onLongitudeChange(v: String) { uiState = uiState.copy(longitude = v) }
    fun onMapsLinkChange(v: String) { uiState = uiState.copy(mapsLink = v) }

    fun save() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val lat = uiState.latitude.trim().toDoubleOrNull()
            val lng = uiState.longitude.trim().toDoubleOrNull()
            repository.addCustomPlace(
                CustomPlaceInput(
                    title = uiState.title,
                    location = uiState.location,
                    notes = uiState.notes,
                    latitude = lat,
                    longitude = lng,
                    mapsLink = uiState.mapsLink,
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
