package com.example.asan_service.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.asan_service.entity.NickNameEntity
import com.example.asan_service.entity.WatchItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchItemDao {
    @Query("SELECT * FROM watch_item_entities")
    fun getAll(): Flow<List<WatchItemEntity>>

    @Query("SELECT * FROM watch_item_entities ")
    fun getAllConnected(): LiveData<List<WatchItemEntity>>

    @Query("SELECT * FROM watch_item_entities ORDER BY watchId")
    fun getAllSortedByWatchId(): LiveData<List<WatchItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(watchItems: List<WatchItemEntity>)

    @Delete
    fun delete(watchItem: WatchItemEntity)

    @Query("UPDATE watch_item_entities SET patientName = :newName WHERE watchId = :watchId")
    fun updatePatientName(watchId: String, newName: String)
    @Query("DELETE FROM watch_item_entities")
    fun deleteAll()
}