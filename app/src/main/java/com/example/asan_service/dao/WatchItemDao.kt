package com.example.asan_service.dao

import androidx.room.*
import com.example.asan_service.entity.WatchItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchItemDao {
    @Query("SELECT * FROM watch_item_entities")
    fun getAll(): Flow<List<WatchItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(watchItems: List<WatchItemEntity>)

    @Delete
    fun delete(watchItem: WatchItemEntity)
}