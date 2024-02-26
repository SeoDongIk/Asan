package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asan_service.entity.AccXEntity
import com.example.asan_service.entity.BaroEntity
import com.example.asan_service.entity.GyroXEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GyroXDao {
    @Query("SELECT * FROM gyroxentity WHERE watchId = :watchId ORDER BY id DESC LIMIT 1000")
    fun getOldestData(watchId: String): Flow<List<GyroXEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: GyroXEntity)

    @Query("DELETE FROM GyroXEntity")
    fun deleteAllData()
}