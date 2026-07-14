package com.example.wanderlust.data.repository

import com.example.wanderlust.data.model.CurrentWeather
import com.example.wanderlust.data.remote.WeatherClient

class WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<CurrentWeather?> {
        return runCatching {
            WeatherClient.getCurrentWeather(latitude, longitude).current_weather
        }
    }
}
