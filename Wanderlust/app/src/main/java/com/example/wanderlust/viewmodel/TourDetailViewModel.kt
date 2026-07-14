package com.example.wanderlust.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TourDetailViewModel(
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(),
) : ViewModel() {

    var isSaved by mutableStateOf(false)
        private set
    var saveMessage by mutableStateOf<String?>(null)
        private set
    var weatherText by mutableStateOf("Loading weather...")
        private set

    fun clearSaveMessage() {
        saveMessage = null
    }

    fun loadWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val weather = withContext(Dispatchers.IO) {
                com.example.wanderlust.data.repository.WeatherRepository()
                    .getCurrentWeather(latitude, longitude)
                    .getOrNull()
            }
            weatherText = if (weather != null) {
                "${weather.temperature.toInt()}°C • ${com.example.wanderlust.data.weatherLabel(weather.weathercode)}"
            } else {
                "—"
            }
        }
    }

    fun loadSavedState(tourId: String, title: String) {
        viewModelScope.launch {
            isSaved = favoriteRepository.isFavorite(tourId, title)
        }
    }

    fun toggleSave(destination: DestinationCard) {
        val tour = Tour(
            id = destination.id,
            title = destination.title,
            description = destination.description,
            category = destination.category,
            rating = destination.rating,
        )
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(tour)
                .onSuccess { saved ->
                    isSaved = saved
                    saveMessage = if (saved) "saved" else "removed"
                }
                .onFailure {
                    saveMessage = "error"
                }
        }
    }
}
