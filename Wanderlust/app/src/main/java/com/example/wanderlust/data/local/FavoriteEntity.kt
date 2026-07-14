package com.example.wanderlust.data.local

import androidx.room.Entity

@Entity(tableName = "favorite_tours", primaryKeys = ["userId", "tourId"])
data class FavoriteEntity(
    val userId: String,
    val tourId: String,
    val title: String,
    val description: String,
    val category: String,
    val rating: Double,
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isCustom: Boolean = false,
)
