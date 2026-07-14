package com.example.wanderlust.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherClient {
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    suspend fun getCurrentWeather(latitude: Double, longitude: Double) =
        api.getCurrentWeather(latitude = latitude, longitude = longitude)
}
