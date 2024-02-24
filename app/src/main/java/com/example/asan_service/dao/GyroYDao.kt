package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.asan_service.entity.GyroYEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GyroYDao {
    @Query("SELECT * FROM gyroyentity WHERE watchId = :watchId ORDER BY id ASC LIMIT 60")
    fun getOldestData(watchId: String): Flow<List<GyroYEntity>>

    @Insert
    fun insertData(data: GyroYEntity)

    @Query("DELETE FROM GyroYEntity")
    fun deleteAllData()
}