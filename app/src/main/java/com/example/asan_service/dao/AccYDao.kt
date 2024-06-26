package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asan_service.entity.AccXEntity
import com.example.asan_service.entity.AccYEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccYDao {
//    @Query("SELECT * FROM AccYEntity WHERE watchId = :watchId ORDER BY id DESC LIMIT 12000")
//    fun getOldestData(watchId: String): Flow<List<AccYEntity>>

    @Query("SELECT * FROM AccYEntity WHERE watchId = :watchId AND (id, timeStamp) IN (SELECT MAX(id), timeStamp FROM AccYEntity WHERE watchId = :watchId GROUP BY timeStamp) ORDER BY timeStamp DESC LIMIT 12000")
    fun getOldestData(watchId: String): Flow<List<AccYEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: AccYEntity)

    @Query("DELETE FROM AccYEntity")
    fun deleteAllData()
}