package com.example.wanderlust.data.model

data class AdminTourRequest(
    val title: String,
    val description: String,
    val category: String,
    val rating: Double,
)

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
)

data class AdminAnalytics(
    val tours: Int,
    val users: Int,
    val averageRating: Double,
    val topCategory: String,
)
