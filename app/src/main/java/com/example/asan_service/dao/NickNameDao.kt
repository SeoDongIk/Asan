package com.example.asan_service.dao

import androidx.room.*
import com.example.asan_service.entity.NickNameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NickNameDao {
    @Query("SELECT * FROM nicknameentity")
    fun getAll(): Flow<List<NickNameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nickName : NickNameEntity)
}