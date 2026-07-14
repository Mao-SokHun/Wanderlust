package com.example.wanderlust.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TourDao {
    @Query("SELECT * FROM tour_cache ORDER BY id")
    suspend fun getAll(): List<TourEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tours: List<TourEntity>)

    @Query("DELETE FROM tour_cache")
    suspend fun clear()
}
