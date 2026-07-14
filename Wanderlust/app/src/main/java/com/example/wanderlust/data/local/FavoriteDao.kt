package com.example.wanderlust.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_tours WHERE userId = :userId ORDER BY tourId DESC")
    suspend fun getForUser(userId: String): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<FavoriteEntity>)

    @Query("DELETE FROM favorite_tours WHERE userId = :userId")
    suspend fun clearForUser(userId: String)

    @Query("DELETE FROM favorite_tours WHERE userId = :userId AND tourId = :tourId")
    suspend fun remove(userId: String, tourId: String)

    @Query("SELECT COUNT(*) FROM favorite_tours WHERE userId = :userId AND tourId = :tourId")
    suspend fun isFavorite(userId: String, tourId: String): Int

    @Query(
        "SELECT COUNT(*) FROM favorite_tours WHERE userId = :userId AND LOWER(title) = LOWER(:title)",
    )
    suspend fun isFavoriteByTitle(userId: String, title: String): Int

    @Query("SELECT * FROM favorite_tours WHERE userId = :userId AND isCustom = 1 ORDER BY tourId DESC")
    suspend fun getCustomForUser(userId: String): List<FavoriteEntity>
}
