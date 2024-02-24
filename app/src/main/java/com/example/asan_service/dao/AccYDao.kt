package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.asan_service.entity.AccYEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccYDao {
    @Query("SELECT * FROM AccYEntity WHERE watchId = :watchId ORDER BY id ASC LIMIT 60")
    fun getOldestData(watchId: String): Flow<List<AccYEntity>>

    @Insert
    fun insertData(data: AccYEntity)

    @Query("DELETE FROM AccYEntity")
    fun deleteAllData()
}