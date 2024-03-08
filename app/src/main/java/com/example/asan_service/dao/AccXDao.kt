package com.example.asan_service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asan_service.entity.AccXEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccXDao {
    @Query("SELECT * FROM accxentity WHERE watchId = :watchId ORDER BY id DESC LIMIT 12000")
    fun getOldestData(watchId: String): Flow<List<AccXEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: AccXEntity)

    @Query("DELETE FROM accxentity")
    fun deleteAllData()
}