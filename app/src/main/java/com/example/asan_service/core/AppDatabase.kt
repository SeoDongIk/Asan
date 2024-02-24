package com.example.asan_service.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.entity.WatchItemEntity

@Database(entities = [WatchItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchItemDao(): WatchItemDao
}