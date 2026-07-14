package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val places: List<DestinationCard> = emptyList(),
    val errorMessage: String? = null,
)

class FavoritesViewModel(
    private val repository: FavoriteRepository = FavoriteRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(FavoritesUiState())
        private set

    fun refreshIfNeeded(refreshKey: Int) {
        // Always reload when Saved opens or after a new save — stale cache was hiding nearby saves.
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.places.isEmpty(), errorMessage = null)
            repository.loadSavedDestinations()
                .onSuccess { places ->
                    uiState = uiState.copy(
                        isLoading = false,
                        places = places.distinctBy { it.id },
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message,
                        places = emptyList(),
                    )
                }
        }
    }
}
