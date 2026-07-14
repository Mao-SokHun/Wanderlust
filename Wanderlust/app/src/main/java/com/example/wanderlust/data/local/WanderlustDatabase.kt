package com.example.wanderlust.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TourEntity::class, FavoriteEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class WanderlustDatabase : RoomDatabase() {
    abstract fun tourDao(): TourDao
    abstract fun favoriteDao(): FavoriteDao
}
