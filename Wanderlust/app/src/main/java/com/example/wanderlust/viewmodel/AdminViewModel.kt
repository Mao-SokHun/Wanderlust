package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.repository.AdminRepository
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val activeTours: Int? = null,
    val users: Int? = null,
    val errorMessage: String? = null,
)

class AdminViewModel(
    private val repository: AdminRepository = AdminRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(AdminUiState())
        private set

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            repository.getStats()
                .onSuccess { stats ->
                    uiState = uiState.copy(
                        isLoading = false,
                        activeTours = stats.activeTours,
                        users = stats.users,
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Cannot load admin data",
                    )
                }
        }
    }
}
