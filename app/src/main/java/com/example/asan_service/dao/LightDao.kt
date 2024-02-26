package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asan_service.entity.AccXEntity
import com.example.asan_service.entity.HeartRateEntity
import com.example.asan_service.entity.LightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LightDao {
    @Query("SELECT * FROM lightentity WHERE watchId = :watchId ORDER BY id DESC LIMIT 1000")
    fun getOldestData(watchId: String): Flow<List<LightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: LightEntity)

    @Query("DELETE FROM LightEntity")
    fun deleteAllData()
}