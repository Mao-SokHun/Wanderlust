package com.example.wanderlust.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wanderlust.data.model.Tour

@Entity(tableName = "tour_cache")
data class TourEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val rating: Double,
)

fun TourEntity.toDomain(): Tour = Tour(id, title, description, category, rating)

fun Tour.toEntity(): TourEntity = TourEntity(id, title, description, category, rating)
