package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.DestinationCatalog
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.repository.TourRepository
import com.example.wanderlust.data.repository.TourRepositoryProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = false,
    val tours: List<Tour> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
    val isOfflineData: Boolean = false,
)

class ExploreViewModel(
    private val repository: TourRepository = TourRepositoryProvider.instance,
) : ViewModel() {

    var uiState by mutableStateOf(ExploreUiState())
        private set

    private var searchJob: Job? = null
    private var loadedOnce = false

    init {
        viewModelScope.launch {
            val cached = repository.getCachedTours()
            val cambodiaHits = cached.count { DestinationCatalog.findByTitle(it.title) != null }
            // Stale world-tour demos left in Room from older API seeds
            if (cached.isNotEmpty() && cambodiaHits > 0) {
                uiState = uiState.copy(tours = cached, isOfflineData = true)
            } else if (cached.isNotEmpty() && cambodiaHits == 0) {
                repository.clearCache()
            }
            loadTours(showSpinner = cached.isEmpty() || cambodiaHits == 0)
        }
    }

    fun setInitialQuery(query: String) {
        if (query.isNotBlank() && uiState.searchQuery.isBlank()) {
            uiState = uiState.copy(searchQuery = query)
            loadTours()
        }
    }

    fun updateSearch(query: String) {
        uiState = uiState.copy(searchQuery = query)
        searchDebounced()
    }

    fun selectCategory(category: String?) {
        uiState = uiState.copy(selectedCategory = category)
        loadTours(showSpinner = false)
    }

    fun loadTours(showSpinner: Boolean = !loadedOnce) {
        viewModelScope.launch {
            if (showSpinner) {
                uiState = uiState.copy(isLoading = true, errorMessage = null)
            }
            repository.getTours(
                search = uiState.searchQuery,
                category = uiState.selectedCategory,
            )
                .onSuccess { tours ->
                    loadedOnce = true
                    uiState = uiState.copy(
                        isLoading = false,
                        tours = tours,
                        isOfflineData = false,
                        errorMessage = null,
                    )
                }
                .onFailure { error ->
                    loadedOnce = true
                    val cached = repository.getCachedTours()
                    uiState = uiState.copy(
                        isLoading = false,
                        tours = if (cached.isNotEmpty()) cached else uiState.tours,
                        isOfflineData = cached.isNotEmpty(),
                        errorMessage = if (cached.isEmpty()) error.message else null,
                    )
                }
        }
    }

    fun searchDebounced() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            loadTours(showSpinner = false)
        }
    }

    fun search() = searchDebounced()
}
