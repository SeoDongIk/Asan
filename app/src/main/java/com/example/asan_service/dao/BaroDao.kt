package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asan_service.entity.BaroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BaroDao {
    @Query("SELECT * FROM baroentity WHERE watchId = :watchId ORDER BY id DESC LIMIT 1000")
    fun getOldestData(watchId: String): Flow<List<BaroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: BaroEntity)

    @Query("DELETE FROM BaroEntity")
    fun deleteAllData()
}