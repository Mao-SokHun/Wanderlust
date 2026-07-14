package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.model.AdminAnalytics
import com.example.wanderlust.data.model.AdminTourRequest
import com.example.wanderlust.data.model.AdminUser
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.repository.AdminRepository
import kotlinx.coroutines.launch

data class AdminToolsUiState(
    val isLoading: Boolean = false,
    val tours: List<Tour> = emptyList(),
    val users: List<AdminUser> = emptyList(),
    val analytics: AdminAnalytics? = null,
    val message: String? = null,
    val errorMessage: String? = null,
)

class AdminToolsViewModel(
    private val repository: AdminRepository = AdminRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(AdminToolsUiState())
        private set

    fun clearMessage() {
        uiState = uiState.copy(message = null, errorMessage = null)
    }

    fun loadTours() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, message = null)
            repository.getTours()
                .onSuccess { tours ->
                    uiState = uiState.copy(isLoading = false, tours = tours)
                }
                .onFailure { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message ?: "Cannot load tours")
                }
        }
    }

    fun addTour(request: AdminTourRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, message = null)
            repository.addTour(request)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, message = "Tour added successfully")
                    loadTours()
                }
                .onFailure { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message ?: "Cannot add tour")
                }
        }
    }

    fun updateTour(id: String, request: AdminTourRequest) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, message = null)
            repository.updateTour(id, request)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, message = "Tour updated successfully")
                    loadTours()
                }
                .onFailure { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message ?: "Cannot update tour")
                }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, message = null)
            repository.getUsers()
                .onSuccess { users ->
                    uiState = uiState.copy(isLoading = false, users = users)
                }
                .onFailure { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message ?: "Cannot load users")
                }
        }
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, message = null)
            repository.getAnalytics()
                .onSuccess { analytics ->
                    uiState = uiState.copy(isLoading = false, analytics = analytics)
                }
                .onFailure { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message ?: "Cannot load analytics")
                }
        }
    }
}
