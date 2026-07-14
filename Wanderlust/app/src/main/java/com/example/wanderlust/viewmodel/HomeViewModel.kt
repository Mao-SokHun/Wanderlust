package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.repository.TourRepository
import com.example.wanderlust.data.repository.TourRepositoryProvider
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val tours: List<Tour> = emptyList(),
    val errorMessage: String? = null,
    val userName: String? = SessionManager.userName,
)

class HomeViewModel(
    private val repository: TourRepository = TourRepositoryProvider.instance,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    fun loadTours(category: String? = null) {
        viewModelScope.launch {
            val cached = repository.getCachedTours()
            if (cached.isNotEmpty() && uiState.tours.isEmpty()) {
                uiState = uiState.copy(tours = cached)
            }
            uiState = uiState.copy(isLoading = uiState.tours.isEmpty(), errorMessage = null)
            repository.getTours(category = category)
                .onSuccess { tours ->
                    uiState = uiState.copy(
                        isLoading = false,
                        tours = tours,
                        userName = SessionManager.userName,
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Cannot connect to server",
                    )
                }
        }
    }
}
